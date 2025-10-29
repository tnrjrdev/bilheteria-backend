package com.rk.bilheteria.payments;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.Locale;

public final class PixEmvBuilder {

    private PixEmvBuilder() {}

    public static String build(
            String pixKey,
            String merchant,
            String city,
            BigDecimal amount,
            String txid,
            String description
    ) {
        // ==== validações obrigatórias ====
        String key = requireNonBlank(pixKey, "pixKey");
        String merchRaw = requireNonBlank(merchant, "merchant");
        String cityRaw  = requireNonBlank(city, "city");
        BigDecimal amt  = requireAmount(amount);
        String tx       = requireNonBlank(txid, "txid");

        // ==== normalizações (Pix/EMV) ====
        // Regras usuais: limitar tamanho, uppercase, remover acentos, filtrar chars estranhos
        String merch = normalize(merchRaw, 25, true);
        String cty   = normalize(cityRaw, 15, true);

        // Txid: até 25 chars (BCB) – sem quebras/espacos; mantém [A-Z0-9-*.@]
        tx = normalizeTxid(tx);

        // Descrição é opcional, máximo 50
        String desc = (description == null || description.isBlank())
                ? ""
                : tlv("02", truncate(description.trim(), 50));

        // ==== tags EMV ====
        String gui = tlv("00","br.gov.bcb.pix");
        String keyTag = tlv("01", key);
        String mai = tlv("26", gui + keyTag + desc);

        String mcc  = tlv("52","0000");
        String curr = tlv("53","986");
        String amtTag = tlv("54", amt.setScale(2, RoundingMode.HALF_UP).toPlainString());
        String ctry = tlv("58","BR");
        String name = tlv("59", merch);
        String cityTag = tlv("60", cty);
        String add  = tlv("62", tlv("05", tx));

        String base = tlv("00","01")     // Payload Format Indicator
                + tlv("01","12")     // Point of Initiation Method (12 = dinâmico, 11 = estático)
                + mai
                + mcc
                + curr
                + amtTag
                + ctry
                + name
                + cityTag
                + add
                + "6304";           // CRC placeholder

        return base + crc16(base);
    }

    // ======== helpers ========

    private static String requireNonBlank(String v, String field) {
        if (v == null || v.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório ausente: " + field);
        }
        return v.trim();
    }

    private static BigDecimal requireAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Campo obrigatório ausente: amount");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount deve ser > 0");
        }
        return amount;
    }

    /** Remove acentos, filtra caracteres não-ASCII imprimíveis e limita tamanho. */
    private static String normalize(String input, int maxLen, boolean upper) {
        String n = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");                  // remove diacríticos
        n = n.replaceAll("[^\\p{Alnum} .\\-@*/:+]", " ");    // mantém A-Z0-9 e poucos pontuadores usuais
        n = n.trim();
        if (upper) n = n.toUpperCase(Locale.ROOT);
        return truncate(n, maxLen);
    }

    private static String normalizeTxid(String txid) {
        String t = Normalizer.normalize(txid, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+","")
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9@\\-\\.*]+", ""); // remove espaços e chars inválidos
        if (t.isEmpty()) {
            throw new IllegalArgumentException("txid inválido");
        }
        return truncate(t, 25);
    }

    private static String truncate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(0, maxLen);
    }

    private static String tlv(String id, String value) {
        String len = String.format(Locale.ROOT, "%02d", value.length());
        return id + len + value;
    }

    // mesmo algoritmo CRC-16/CCITT-FALSE que você já usa
    private static String crc16(String payload) {
        int crc = 0xFFFF;
        for (int i = 0; i < payload.length(); i++) {
            crc ^= (payload.charAt(i) << 8);
            for (int j = 0; j < 8; j++) {
                crc = ((crc & 0x8000) != 0) ? ((crc << 1) ^ 0x1021) : (crc << 1);
                crc &= 0xFFFF;
            }
        }
        return String.format(Locale.ROOT, "%04X", crc);
    }
}
