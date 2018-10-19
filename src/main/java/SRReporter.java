class SRReporter {

    String build(String authorAsMention, String title, String cat1Title, Integer cat1Value, String cat2Title, Integer cat2Value, Integer cat3Value) {
        StringBuilder srReport = new StringBuilder();
        srReport.append(authorAsMention);
        srReport.append("'s ");
        srReport.append(title);
        srReport.append("\r------------------------\r");
        srReport.append(cat1Title);
        srReport.append(" SR: ");
        srReport.append(cat1Value);
        srReport.append("\r");
        srReport.append(cat2Title);
        srReport.append(" SR: ");
        srReport.append(cat2Value);
        srReport.append("\r");
        srReport.append("Difference: ");
        if (cat3Value > 0) {
            srReport.append("+");
        }
        srReport.append(cat3Value);
        srReport.append("\r------------------------\r");
        return srReport.toString();
    }
}
