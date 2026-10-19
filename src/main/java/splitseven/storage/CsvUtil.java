package splitseven.storage;

import java.util.ArrayList;
import java.util.List;

final class CsvUtil {
    private CsvUtil() {
    }

    static String formatRow(List<String> fields) {
        StringBuilder formattedRow = new StringBuilder();

        for (int fieldIndex = 0; fieldIndex < fields.size(); fieldIndex++) {
            if (fieldIndex > 0) {
                formattedRow.append(',');
            }

            formattedRow.append(escape(fields.get(fieldIndex)));
        }

        return formattedRow.append('\n').toString();
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }

        boolean needsQuotes = value.contains(",")
                || value.contains("\"")
                || value.contains("\n")
                || value.contains("\r");
        if (needsQuotes) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    /** Parses CSV text; quoted fields may contain commas, quotes, and newlines. */
    static List<List<String>> parse(String text) {
        List<List<String>> rows = new ArrayList<>();
        List<String> currentRow = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean insideQuotedField = false;

        for (int characterIndex = 0; characterIndex < text.length(); characterIndex++) {
            char character = text.charAt(characterIndex);

            if (insideQuotedField) {
                if (character == '"') {
                    boolean hasEscapedQuote = characterIndex + 1 < text.length()
                            && text.charAt(characterIndex + 1) == '"';
                    if (hasEscapedQuote) {
                        currentField.append('"');
                        characterIndex++;
                    } else {
                        insideQuotedField = false;
                    }
                } else {
                    currentField.append(character);
                }
            } else if (character == '"') {
                insideQuotedField = true;
            } else if (character == ',') {
                currentRow.add(currentField.toString());
                currentField.setLength(0);
            } else if (character == '\n' || character == '\r') {
                boolean isWindowsLineEnding = character == '\r'
                        && characterIndex + 1 < text.length()
                        && text.charAt(characterIndex + 1) == '\n';
                if (isWindowsLineEnding) {
                    characterIndex++;
                }

                currentRow.add(currentField.toString());
                currentField.setLength(0);
                rows.add(currentRow);
                currentRow = new ArrayList<>();
            } else {
                currentField.append(character);
            }
        }

        boolean hasRemainingField = currentField.length() > 0 || !currentRow.isEmpty();
        if (hasRemainingField) {
            currentRow.add(currentField.toString());
            rows.add(currentRow);
        }

        return rows;
    }
}
