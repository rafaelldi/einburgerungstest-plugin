package me.rafaelldi.einburgerungstest.questions

internal enum class CategoryGroup {
    NATIONAL,
    REGIONAL
}

internal enum class QuestionCategory(val displayName: String, val group: CategoryGroup) {
    BildungUndArbeit("Bildung und Arbeit", CategoryGroup.NATIONAL),
    BundUndLaender("Bund und Länder", CategoryGroup.NATIONAL),
    EuropaUndWelt("Europa und Welt", CategoryGroup.NATIONAL),
    Geschichte("Geschichte", CategoryGroup.NATIONAL),
    GesellschaftUndFamilie("Gesellschaft und Familie", CategoryGroup.NATIONAL),
    Politik("Politik", CategoryGroup.NATIONAL),
    Recht("Recht", CategoryGroup.NATIONAL),
    ReligionUndKultur("Religion und Kultur", CategoryGroup.NATIONAL),
    Staat("Staat", CategoryGroup.NATIONAL),
    Wirtschaft("Wirtschaft", CategoryGroup.NATIONAL),
    BadenWuerttemberg("Baden-Württemberg", CategoryGroup.REGIONAL),
    Bayern("Bayern", CategoryGroup.REGIONAL),
    Berlin("Berlin", CategoryGroup.REGIONAL),
    Brandenburg("Brandenburg", CategoryGroup.REGIONAL),
    Bremen("Bremen", CategoryGroup.REGIONAL),
    Hamburg("Hamburg", CategoryGroup.REGIONAL),
    Hessen("Hessen", CategoryGroup.REGIONAL),
    MecklenburgVorpommern("Mecklenburg-Vorpommern", CategoryGroup.REGIONAL),
    Niedersachsen("Niedersachsen", CategoryGroup.REGIONAL),
    NordrheinWestfalen("Nordrhein-Westfalen", CategoryGroup.REGIONAL),
    RheinlandPfalz("Rheinland-Pfalz", CategoryGroup.REGIONAL),
    Sachsen("Sachsen", CategoryGroup.REGIONAL),
    SachsenAnhalt("Sachsen-Anhalt", CategoryGroup.REGIONAL),
    SchleswigHolstein("Schleswig-Holstein", CategoryGroup.REGIONAL),
    Thueringen("Thüringen", CategoryGroup.REGIONAL);

    companion object {
        val nationalCategories: List<QuestionCategory> =
            entries.filter { it.group == CategoryGroup.NATIONAL }

        val regionalCategories: List<QuestionCategory> =
            entries.filter { it.group == CategoryGroup.REGIONAL }
    }
}
