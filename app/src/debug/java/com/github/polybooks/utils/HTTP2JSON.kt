package com.github.polybooks.utils

import com.google.gson.JsonParser
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture

val urlRegex = """.*openlibrary.org(.*)""".toRegex()
val url2filename = mapOf(
    "/languages/eng.json" to "eng.json",
    "/languages/fre.json" to "fre.json",
    "/authors/OL52830A.json" to "OL52830A.json",
    "/authors/OL7511250A.json" to "OL7511250A.json",
    "/authors/OL7482089A.json" to "OL7482089A.json",
    "/authors/OL8315711A.json" to  "OL8315711A.json",
    "/authors/OL8315712A.json" to  "OL8315712A.json",
    "/authors/OL6899222A.json" to "OL6899222A.json",
    "/authors/OL752714A.json" to "OL752714A.json",
    "/isbn/0030137314.json" to "0030137314.json",
    "/isbn/9782376863069.json" to "9782376863069.json",
    "/isbn/2376863066.json" to "9782376863069.json",
    "/isbn/9781985086593.json" to "9781985086593.json",
    "/isbn/9780156881807.json" to "9780156881807.json",
    "/isbn/9781603090476.json" to "9781603090476.json"
)
const val baseDir = "src/test/java/com/github/polybooks/database"
val url2json = { url : String ->
    CompletableFuture.supplyAsync {
        val regexMatch = urlRegex.matchEntire(url) ?: throw FileNotFoundException("File Not Found : $url")
        val address = regexMatch.groups[1]?.value ?: throw Error("The regex is wrong")
        if (url2files.containsKey(address)) JsonParser.parseString(url2files[address])
        else throw FileNotFoundException("File Not Found : $url")
    }
}

private val url2files = mapOf(
    "/languages/eng.json" to "{\n" +
            "  \"comment\": \"initial import\",\n" +
            "  \"code\": \"eng\",\n" +
            "  \"name\": \"English\",\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2008-09-02 19:13:16.977246\"\n" +
            "  },\n" +
            "  \"key\": \"/languages/eng\",\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/language\"\n" +
            "  },\n" +
            "  \"id\": 9887664,\n" +
            "  \"revision\": 3\n" +
            "}",
    "/languages/fre.json" to "{\n" +
            "  \"comment\": \"initial import\",\n" +
            "  \"code\": \"fre\",\n" +
            "  \"latest_revision\": 5,\n" +
            "  \"name\": \"French / fran\\u00e7ais\",\n" +
            "  \"key\": \"/languages/fre\",\n" +
            "  \"created\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2008-04-04T15:49:50.080837\"\n" +
            "  },\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/language\"\n" +
            "  },\n" +
            "  \"m\": \"edit\",\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2020-04-07T20:17:00.983838\"\n" +
            "  },\n" +
            "  \"revision\": 5\n" +
            "}",
    "/authors/OL52830A.json" to "{\n" +
            "  \"name\": \"Eug\\u00e8ne Ionesco\",\n" +
            "  \"links\": [\n" +
            "    {\n" +
            "      \"url\": \"http://en.wikipedia.org/wiki/Eug%C3%A8ne_Ionesco\",\n" +
            "      \"type\": {\n" +
            "        \"key\": \"/type/link\"\n" +
            "      },\n" +
            "      \"title\": \"Wikipedia\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"personal_name\": \"Euge\\u0300ne Ionesco\",\n" +
            "  \"death_date\": \"28 March 1994\",\n" +
            "  \"alternate_names\": [\n" +
            "    \"Eugene Ionesco\",\n" +
            "    \"Eug\\u00e8ne Ionesco\",\n" +
            "    \"Eugen Ionescu\",\n" +
            "    \"Euge  ne Ionesco\",\n" +
            "    \"Ionesco\",\n" +
            "    \"E. Ionesco\",\n" +
            "    \"Eugene (trans Richard Seaver). Ionesco\",\n" +
            "    \"Euge\\u00cc\\u0080ne Ionesco\",\n" +
            "    \"Eug\\u00c3\\u00a8ne Ionesco\",\n" +
            "    \"Euge`ne Ionesco\",\n" +
            "    \"Euge\\u0301ne Ionesco\"\n" +
            "  ],\n" +
            "  \"created\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2008-04-01T03:28:50.625462\"\n" +
            "  },\n" +
            "  \"photos\": [\n" +
            "    6882794\n" +
            "  ],\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2020-09-27T08:03:14.582869\"\n" +
            "  },\n" +
            "  \"latest_revision\": 7,\n" +
            "  \"key\": \"/authors/OL52830A\",\n" +
            "  \"birth_date\": \"1912\",\n" +
            "  \"revision\": 7,\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/author\"\n" +
            "  },\n" +
            "  \"remote_ids\": {\n" +
            "    \"viaf\": \"17224288\",\n" +
            "    \"wikidata\": \"Q46706\",\n" +
            "    \"isni\": \"0000000120993554\"\n" +
            "  }\n" +
            "}",
    "/authors/OL7511250A.json" to "{\n" +
            "  \"name\": \"Megan Lindholm\",\n" +
            "  \"created\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2019-05-10T22:15:05.166954\"\n" +
            "  },\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2019-05-10T22:15:05.166954\"\n" +
            "  },\n" +
            "  \"latest_revision\": 1,\n" +
            "  \"key\": \"/authors/OL7511250A\",\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/author\"\n" +
            "  },\n" +
            "  \"revision\": 1\n" +
            "}",
    "/authors/OL7482089A.json" to "{\n" +
            "  \"name\": \"Steven Brust\",\n" +
            "  \"created\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2019-03-18T05:13:27.668038\"\n" +
            "  },\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2019-03-18T05:13:27.668038\"\n" +
            "  },\n" +
            "  \"latest_revision\": 1,\n" +
            "  \"key\": \"/authors/OL7482089A\",\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/author\"\n" +
            "  },\n" +
            "  \"revision\": 1\n" +
            "}",
    "/authors/OL8315711A.json" to  "{\n" +
            "  \"name\": \"Remzi H Arpaci-Dusseau\",\n" +
            "  \"created\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2020-08-28T14:16:29.504385\"\n" +
            "  },\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2020-08-28T14:16:29.504385\"\n" +
            "  },\n" +
            "  \"latest_revision\": 1,\n" +
            "  \"key\": \"/authors/OL8315711A\",\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/author\"\n" +
            "  },\n" +
            "  \"revision\": 1\n" +
            "}",
    "/authors/OL8315712A.json" to  "{\n" +
            "  \"name\": \"Andrea C Arpaci-Dusseau\",\n" +
            "  \"created\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2020-08-28T14:16:29.504385\"\n" +
            "  },\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2020-08-28T14:16:29.504385\"\n" +
            "  },\n" +
            "  \"latest_revision\": 1,\n" +
            "  \"key\": \"/authors/OL8315712A\",\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/author\"\n" +
            "  },\n" +
            "  \"revision\": 1\n" +
            "}",
    "/authors/OL6899222A.json" to "{\n" +
            "  \"bio\": {\n" +
            "    \"type\": \"/type/text\",\n" +
            "    \"value\": \"Moli\\u00e8re (Jean-Baptiste Poquelin) was born in Paris on January 15, 1622. His father was one of eight valets de chambre tapissiers who tended the king's furniture and upholstery, so the young Poquelin received every advantage a boy could wish for. He was educated at the finest schools (the College de Clermont in Paris.) He had access to the king's court. But even as a child, Moli\\u00e8re found it infinitely more pleasant to poke fun at the aristocracy than to associate with them. As a young boy, he learned that he could cause quite a stir by mimicking his mother's priest. His mother, a deeply religious woman, might have broken the young satirist of this habit had she not died before he was yet twelve-years-old. His father soon remarried, but in less than three years, this wife also passed away. At the age of fifteen, Jean-Baptiste was left alone with his father and was most likely apprenticed to his trade.\\r\\n\\r\\nThe boy never showed much of an interest for the business of upholstering. Fortunately, his father's shop was located near two important theatrical sites: the Pont-Neuf and the H\\u00f4tel de Bourgogne. At the Pont-Neuf, comedians performed plays and farces in the street in order to sell patent medicines to the crowds. Although not traditional theatre in the strictest sense, the antics of these comic medicine-men brought a smile to Jean-Baptiste's face on many an afternoon. At the H\\u00f4tel de Bourgogne--which the boy attended with his grandfather--the King's Players performed more traditional romantic tragedies and broad farces. Apparently, these two theatrical venues had quite an impact on the young Poquelin, for in 1643, at the age of twenty-one, he decided to dedicate his life to the theatre.\\r\\n\\r\\nJean-Baptiste had fallen in love with a beautiful red-headed actress named Madeleine B\\u00e9jart. Along with Madeleine, her brother Joseph and sister Genevieve, and about a dozen other young well-to-do hopefuls, Jean-Baptiste founded a dramatic troupe called The Illustrious Theater. It was about this time that he changed his name to Moli\\u00e8re, probably to spare his father the embarrassment of having an actor in the family.\\r\\n\\r\\nMoli\\u00e8re and his companions made their dramatic debut in a converted tennis court. Although the company was brimming with enthusiasm, none of them had much experience and when they began to charge admission, the results proved disastrous. Over the course of the next two years, the little company appeared in three different theatres in various parts of Paris, and each time, they failed miserably. Several of the original members dropped out of the company during this period. Finally, the seven remaining actors decided to forget Paris and go on a tour of the provinces. For the next twelve years, they would travel from town to town, performing and honing their craft.\\r\\n\\r\\nIt was during this period that Moli\\u00e8re began to write plays for the company. His first important piece, L'\\u00c9tourdi or The Blunderer, followed the escapades of Mascarille, a shrewd servant who sets about furthering his master's love affair with a young woman only to have his plans thwarted when the blundering lover inadvertantly interferes. The five-act piece proved quite successful, and a number of other works followed. By the spring of 1658, Moli\\u00e8re and his much-improved company decided to try their luck once more in Paris. When they learned that the King's brother, the Duke of Anjou, was said to be interested in supporting a dramatic company which would bear his name, they immediately set about gaining an introduction to the Court.\\r\\n\\r\\nOn the evening of October 24, 1658, Moli\\u00e8re and his troupe performed for the first time before Louis XIV and his courtiers in the Guard Room of the old Louvre Palace. They made a crucial mistake, however, by performing a tragedy (Cornielle's second-rate Nicom\\u00e9de) instead of one of their popular farces. The Court was not impressed. Fortunately Moli\\u00e8re, realizing their blunder, approached the King at the conclusion of the tragedy and asked permission to perform one of his own plays, The Love-Sick Doctor. The King granted his request, and the play was such a success that the little company--which would thereafter be known as the Troupe de Monsieur--was granted use of the H\\u00f4tel du Petit Bourbon, one of the three most important theaters in Paris.\\r\\n\\r\\nThe first of Moli\\u00e8re's plays to be presented at the Petit Bourbon was Les Pr\\u00e9cieuses Ridicules or The Pretentious Ladies which satirized Madame de Rambouillet, a member of the King's court who had set herself up as the final judge of taste and culture in Paris. The play proved so successful that Moli\\u00e8re doubled the price of admission and was invited to give a special performance for the King. The King was delighted and rewarded the playwright with a large gift of cash, but Moli\\u00e8re had made powerful enemies of some of the King's followers. Madame de Rambouillet and her coterie managed to have performances of the play suspended for fourteen days and, in an attempt to drive Moli\\u00e8re from the city, eventually managed to have the Petit Bourbon closed down completely. But the King immediately granted Moli\\u00e8re use of the Th\\u00e9\\u00e2tre du Palais Royal where he would continue to perform for the rest of his life.\\r\\n\\r\\nOver the course of the next thirteen years, Moli\\u00e8re worked feverishly to make his company the most respected dramatic troupe in Paris. (Eventually, they were awarded the coveted title \\\"Troupe of the King.\\\") He directed his own plays and often played the leading role himself.\\r\\n\\r\\nOn February 17, 1673, Moli\\u00e8re suffered a hemorrhage while playing the role of the hypochondriac Argan in The Imaginary Invalid. He had insisted on going through with the performance in spite of the advice of his wife and friends saying, \\\"There are fifty poor workers who have only their daily wage to live on. What will become of them if the performance does not take place?\\\" He passed away later that night at his home on the Rue Richelieu. The local priests refused to take his confession, for actors had no social standing and had been excommunicated by the church. Nor would they permit him to be buried in holy ground. Four days later, the King interceded and Moli\\u00e8re was finally buried in the Cemetery Saint Joseph under the cover of darkness.\\r\\n\\r\\nMoli\\u00e8re left behind a body of work which not only changed the face of French classical comedy, but has gone on to influence the work of other dramatists the world over. The greatest of his plays include The School for Husbands (1661), The School for Wives (1662), The Misanthrope (1666), The Doctor in Spite of Himself (1666), Tartuffe (1664,1667,1669), The Miser (1668), and The Imaginary Invalid (1673).\\r\\n\\r\\n[Source][1]\\r\\n\\r\\n\\r\\n  [1]: http://www.imagi-nation.com/moonstruck/clsc35.html\"\n" +
            "  },\n" +
            "  \"name\": \"Moli\\u00e8re\",\n" +
            "  \"personal_name\": \"Moli\\u00e8re\",\n" +
            "  \"death_date\": \"17 Feb 1673\",\n" +
            "  \"alternate_names\": [\n" +
            "    \"Jean-Baptiste Moliere\",\n" +
            "    \"J. B. P. Moliere\",\n" +
            "    \"Moliere.\",\n" +
            "    \"Jean Baptiste Moliere\",\n" +
            "    \"Molie?re\",\n" +
            "    \"J.B.P.De Moliere\",\n" +
            "    \"Moliere (Poquelin)\",\n" +
            "    \"Poquelin) Moliere (Poquelin)\",\n" +
            "    \"Jean Baptiste Poquelin de Moliere\",\n" +
            "    \"Jean-baptiste Moliere\",\n" +
            "    \"Molie<re\",\n" +
            "    \"Jean Baptiste Poquelin Moliere\",\n" +
            "    \"Jean B. Moliere\",\n" +
            "    \"Moliere                      Jb\",\n" +
            "    \"Jean Baptiste, Moliere\",\n" +
            "    \"Molie`re\",\n" +
            "    \"J. B. P. de Moliere\",\n" +
            "    \"Jean-Baptiste Poquelin Moliere\",\n" +
            "    \"Jean Baptiste Poquelin Molie`re\",\n" +
            "    \"J Moliere\",\n" +
            "    \"Moliere\",\n" +
            "    \"Jean Baptiste Poquelin de Moli\\u00e8re\",\n" +
            "    \"Jean Baptiste Moli\\u00e8re\",\n" +
            "    \"Jean-Baptiste Poquelin dit Moli\\u00e8re\",\n" +
            "    \"Jean-Baptiste Moli\\u00e8re\",\n" +
            "    \"Moli\\u00e9re\",\n" +
            "    \"Jean Baptiste De Moli\\u00e8re\",\n" +
            "    \"Moli&egrave\",\n" +
            "    \"re\",\n" +
            "    \"Moli\\u00fd\\u00fdre\",\n" +
            "    \"Jean Baptiste Poquelin de Moli\\u00e9re\",\n" +
            "    \"Moli\\u00e8re\",\n" +
            "    \"Jean Baptiste Poquel Moli\\u00e8re\",\n" +
            "    \"Moli\\u00e8re (Jean-Baptiste Poquelin)\",\n" +
            "    \"Moli\\u02beere\",\n" +
            "    \"John Baptiste Poqueline de Moli\\u00e8re\",\n" +
            "    \"Moli\\u00e8re.\",\n" +
            "    \"jean Baptiste de Moli\\u00e8re\",\n" +
            "    \"Moliaere\",\n" +
            "    \"Molie re.\",\n" +
            "    \"John Baptiste Poqueline de Molie  re\",\n" +
            "    \"Molie  re\",\n" +
            "    \"Moli ere.\",\n" +
            "    \"Jean-Baptiste Poquelin\",\n" +
            "    \"J. B. Poquelin Moli\\u00e8re\",\n" +
            "    \"de Jean Baptiste Molier\",\n" +
            "    \"Molier\"\n" +
            "  ],\n" +
            "  \"created\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2011-06-18T02:43:12.717960\"\n" +
            "  },\n" +
            "  \"photos\": [\n" +
            "    6882490,\n" +
            "    3367216\n" +
            "  ],\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2020-09-27T07:54:13.262334\"\n" +
            "  },\n" +
            "  \"latest_revision\": 12,\n" +
            "  \"key\": \"/authors/OL6899222A\",\n" +
            "  \"birth_date\": \"15 Jan 1622\",\n" +
            "  \"revision\": 12,\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/author\"\n" +
            "  },\n" +
            "  \"remote_ids\": {\n" +
            "    \"viaf\": \"2474502\",\n" +
            "    \"wikidata\": \"Q687\",\n" +
            "    \"isni\": \"0000000123197131\"\n" +
            "  }\n" +
            "}",
    "/authors/OL752714A.json" to "{\n" +
            "  \"name\": \"Eddie Campbell\",\n" +
            "  \"personal_name\": \"Eddie Campbell\",\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2008-08-24 12:52:35.060297\"\n" +
            "  },\n" +
            "  \"key\": \"/authors/OL752714A\",\n" +
            "  \"birth_date\": \"1955\",\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/author\"\n" +
            "  },\n" +
            "  \"id\": 2258214,\n" +
            "  \"revision\": 2\n" +
            "}",
    "/isbn/0030137314.json" to "{\n" +
            "  \"publishers\": [\n" +
            "    \"Holt, Rinehart and Winston\"\n" +
            "  ],\n" +
            "  \"number_of_pages\": 239,\n" +
            "  \"ia_box_id\": [\n" +
            "    \"IA127609\"\n" +
            "  ],\n" +
            "  \"covers\": [\n" +
            "    6646964\n" +
            "  ],\n" +
            "  \"lc_classifications\": [\n" +
            "    \"PQ2617.O6 R48 1976\"\n" +
            "  ],\n" +
            "  \"latest_revision\": 7,\n" +
            "  \"key\": \"/books/OL5193233M\",\n" +
            "  \"authors\": [\n" +
            "    {\n" +
            "      \"key\": \"/authors/OL52830A\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"ocaid\": \"rhinocros00ione\",\n" +
            "  \"publish_places\": [\n" +
            "    \"New York\"\n" +
            "  ],\n" +
            "  \"languages\": [\n" +
            "    {\n" +
            "      \"key\": \"/languages/fre\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"pagination\": \"xiii, 239, lxiv p. :\",\n" +
            "  \"title\": \"Rhinoce\\u0301ros\",\n" +
            "  \"dewey_decimal_class\": [\n" +
            "    \"842/.9/14\"\n" +
            "  ],\n" +
            "  \"notes\": {\n" +
            "    \"type\": \"/type/text\",\n" +
            "    \"value\": \"Includes bibliographical references.\"\n" +
            "  },\n" +
            "  \"identifiers\": {\n" +
            "    \"goodreads\": [\n" +
            "      \"4556461\"\n" +
            "    ],\n" +
            "    \"librarything\": [\n" +
            "      \"19600\"\n" +
            "    ]\n" +
            "  },\n" +
            "  \"created\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2008-04-01T03:28:50.625462\"\n" +
            "  },\n" +
            "  \"edition_name\": \"2d ed.\",\n" +
            "  \"lccn\": [\n" +
            "    \"75014361\"\n" +
            "  ],\n" +
            "  \"isbn_10\": [\n" +
            "    \"0030137314\"\n" +
            "  ],\n" +
            "  \"publish_date\": \"1976\",\n" +
            "  \"publish_country\": \"nyu\",\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2011-08-12T15:31:05.643879\"\n" +
            "  },\n" +
            "  \"by_statement\": \"Euge\\u0300ne Ionesco ; edited by Reuben Y. Ellison, Stowell C. Goding ; new exercises by Albert Raffanel.\",\n" +
            "  \"works\": [\n" +
            "    {\n" +
            "      \"key\": \"/works/OL674727W\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/edition\"\n" +
            "  },\n" +
            "  \"revision\": 7\n" +
            "}",
    "/isbn/9782376863069.json" to "{\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/edition\"\n" +
            "  },\n" +
            "  \"title\": \"Liavek\",\n" +
            "  \"authors\": [\n" +
            "    {\n" +
            "      \"key\": \"/authors/OL7482089A\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"/authors/OL7511250A\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"publish_date\": \"Jul 03, 2020\",\n" +
            "  \"source_records\": [\n" +
            "    \"amazon:2376863066\"\n" +
            "  ],\n" +
            "  \"publishers\": [\n" +
            "    \"ACTUSF\"\n" +
            "  ],\n" +
            "  \"isbn_10\": [\n" +
            "    \"2376863066\"\n" +
            "  ],\n" +
            "  \"isbn_13\": [\n" +
            "    \"9782376863069\"\n" +
            "  ],\n" +
            "  \"physical_format\": \"paperback\",\n" +
            "  \"notes\": {\n" +
            "    \"type\": \"/type/text\",\n" +
            "    \"value\": \"Source title: Liavek (PERLES D'EPICE) (French Edition)\"\n" +
            "  },\n" +
            "  \"full_title\": \"Liavek\",\n" +
            "  \"covers\": [\n" +
            "    10719435\n" +
            "  ],\n" +
            "  \"works\": [\n" +
            "    {\n" +
            "      \"key\": \"/works/OL24240012W\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"key\": \"/books/OL32058322M\",\n" +
            "  \"latest_revision\": 1,\n" +
            "  \"revision\": 1,\n" +
            "  \"created\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2021-03-20T10:42:42.520319\"\n" +
            "  },\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2021-03-20T10:42:42.520319\"\n" +
            "  }\n" +
            "}",
    "/isbn/2376863066.json" to "{\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/edition\"\n" +
            "  },\n" +
            "  \"title\": \"Liavek\",\n" +
            "  \"authors\": [\n" +
            "    {\n" +
            "      \"key\": \"/authors/OL7482089A\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"/authors/OL7511250A\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"publish_date\": \"Jul 03, 2020\",\n" +
            "  \"source_records\": [\n" +
            "    \"amazon:2376863066\"\n" +
            "  ],\n" +
            "  \"publishers\": [\n" +
            "    \"ACTUSF\"\n" +
            "  ],\n" +
            "  \"isbn_10\": [\n" +
            "    \"2376863066\"\n" +
            "  ],\n" +
            "  \"isbn_13\": [\n" +
            "    \"9782376863069\"\n" +
            "  ],\n" +
            "  \"physical_format\": \"paperback\",\n" +
            "  \"notes\": {\n" +
            "    \"type\": \"/type/text\",\n" +
            "    \"value\": \"Source title: Liavek (PERLES D'EPICE) (French Edition)\"\n" +
            "  },\n" +
            "  \"full_title\": \"Liavek\",\n" +
            "  \"covers\": [\n" +
            "    10719435\n" +
            "  ],\n" +
            "  \"works\": [\n" +
            "    {\n" +
            "      \"key\": \"/works/OL24240012W\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"key\": \"/books/OL32058322M\",\n" +
            "  \"latest_revision\": 1,\n" +
            "  \"revision\": 1,\n" +
            "  \"created\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2021-03-20T10:42:42.520319\"\n" +
            "  },\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2021-03-20T10:42:42.520319\"\n" +
            "  }\n" +
            "}",
    "/isbn/9781985086593.json" to "{\n" +
            "  \"publishers\": [\n" +
            "    \"CreateSpace Independent Publishing Platform\"\n" +
            "  ],\n" +
            "  \"subtitle\": \"Three Easy Pieces\",\n" +
            "  \"covers\": [\n" +
            "    10401368\n" +
            "  ],\n" +
            "  \"physical_format\": \"paperback\",\n" +
            "  \"full_title\": \"Operating Systems Three Easy Pieces\",\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2020-08-28T14:16:29.504385\"\n" +
            "  },\n" +
            "  \"latest_revision\": 1,\n" +
            "  \"key\": \"/books/OL29583638M\",\n" +
            "  \"authors\": [\n" +
            "    {\n" +
            "      \"key\": \"/authors/OL8315711A\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"/authors/OL8315712A\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"source_records\": [\n" +
            "    \"amazon:198508659X\"\n" +
            "  ],\n" +
            "  \"title\": \"Operating Systems\",\n" +
            "  \"notes\": {\n" +
            "    \"type\": \"/type/text\",\n" +
            "    \"value\": \"Source title: Operating Systems: Three Easy Pieces\"\n" +
            "  },\n" +
            "  \"number_of_pages\": 714,\n" +
            "  \"created\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2020-08-28T14:16:29.504385\"\n" +
            "  },\n" +
            "  \"isbn_13\": [\n" +
            "    \"9781985086593\"\n" +
            "  ],\n" +
            "  \"isbn_10\": [\n" +
            "    \"198508659X\"\n" +
            "  ],\n" +
            "  \"publish_date\": \"Sep 01, 2018\",\n" +
            "  \"works\": [\n" +
            "    {\n" +
            "      \"key\": \"/works/OL21738416W\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/edition\"\n" +
            "  },\n" +
            "  \"revision\": 1\n" +
            "}",
    "/isbn/9780156881807.json" to "{\n" +
            "  \"publishers\": [\n" +
            "    \"Harvest Books\"\n" +
            "  ],\n" +
            "  \"number_of_pages\": 180,\n" +
            "  \"ia_box_id\": [\n" +
            "    \"IA147114\"\n" +
            "  ],\n" +
            "  \"covers\": [\n" +
            "    116546\n" +
            "  ],\n" +
            "  \"ia_loaded_id\": [\n" +
            "    \"tartuffecomedyin00moli\"\n" +
            "  ],\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2011-12-16T21:36:31.872666\"\n" +
            "  },\n" +
            "  \"latest_revision\": 9,\n" +
            "  \"key\": \"/books/OL7366233M\",\n" +
            "  \"authors\": [\n" +
            "    {\n" +
            "      \"key\": \"/authors/OL6899222A\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"contributions\": [\n" +
            "    \"Richard Wilbur (Translator)\"\n" +
            "  ],\n" +
            "  \"languages\": [\n" +
            "    {\n" +
            "      \"key\": \"/languages/eng\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"source_records\": [\n" +
            "    \"ia:tartuffecomedyin00moli\"\n" +
            "  ],\n" +
            "  \"title\": \"Tartuffe, by Moliere\",\n" +
            "  \"identifiers\": {\n" +
            "    \"librarything\": [\n" +
            "      \"268115\"\n" +
            "    ],\n" +
            "    \"goodreads\": [\n" +
            "      \"263994\"\n" +
            "    ]\n" +
            "  },\n" +
            "  \"created\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2008-04-29T13:35:46.876380\"\n" +
            "  },\n" +
            "  \"isbn_13\": [\n" +
            "    \"9780156881807\"\n" +
            "  ],\n" +
            "  \"isbn_10\": [\n" +
            "    \"0156881802\"\n" +
            "  ],\n" +
            "  \"publish_date\": \"January 10, 1968\",\n" +
            "  \"works\": [\n" +
            "    {\n" +
            "      \"key\": \"/works/OL6714239W\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/edition\"\n" +
            "  },\n" +
            "  \"first_sentence\": {\n" +
            "    \"type\": \"/type/text\",\n" +
            "    \"value\": \"Children, I Take my leave much vexed in spirit.\"\n" +
            "  },\n" +
            "  \"revision\": 9\n" +
            "}",
    "/isbn/9781603090476.json" to "{\n" +
            "  \"other_titles\": [\n" +
            "    \"Years have pants\"\n" +
            "  ],\n" +
            "  \"publishers\": [\n" +
            "    \"Top Shelf Productions\"\n" +
            "  ],\n" +
            "  \"table_of_contents\": [\n" +
            "    {\n" +
            "      \"level\": 0,\n" +
            "      \"type\": {\n" +
            "        \"key\": \"/type/toc_item\"\n" +
            "      },\n" +
            "      \"title\": \"The King Canute crowd --\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"level\": 0,\n" +
            "      \"type\": {\n" +
            "        \"key\": \"/type/toc_item\"\n" +
            "      },\n" +
            "      \"title\": \"Graffiti kitchen --\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"level\": 0,\n" +
            "      \"type\": {\n" +
            "        \"key\": \"/type/toc_item\"\n" +
            "      },\n" +
            "      \"title\": \"Shorts --\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"level\": 0,\n" +
            "      \"type\": {\n" +
            "        \"key\": \"/type/toc_item\"\n" +
            "      },\n" +
            "      \"title\": \"How to be an artist --\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"level\": 0,\n" +
            "      \"type\": {\n" +
            "        \"key\": \"/type/toc_item\"\n" +
            "      },\n" +
            "      \"title\": \"Little Italy --\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"level\": 0,\n" +
            "      \"type\": {\n" +
            "        \"key\": \"/type/toc_item\"\n" +
            "      },\n" +
            "      \"title\": \"The dead muse --\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"level\": 0,\n" +
            "      \"type\": {\n" +
            "        \"key\": \"/type/toc_item\"\n" +
            "      },\n" +
            "      \"title\": \"The dance of lifey death --\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"level\": 0,\n" +
            "      \"type\": {\n" +
            "        \"key\": \"/type/toc_item\"\n" +
            "      },\n" +
            "      \"title\": \"After the snooter --\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"level\": 0,\n" +
            "      \"type\": {\n" +
            "        \"key\": \"/type/toc_item\"\n" +
            "      },\n" +
            "      \"title\": \"Fragments --\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"level\": 0,\n" +
            "      \"type\": {\n" +
            "        \"key\": \"/type/toc_item\"\n" +
            "      },\n" +
            "      \"title\": \"\\\"The years have pants.\\\"\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"description\": {\n" +
            "    \"type\": \"/type/text\",\n" +
            "    \"value\": \"Collects semi-autobiographical comics from Eddie Campbell that feature his alter-ego, Alec MacGarry, including stories from \\\"The King Canute Crowd,\\\" \\\"Graffiti Kitchen,\\\" \\\"The Dead Muse,\\\" \\\"Fragments,\\\" and other books.\"\n" +
            "  },\n" +
            "  \"local_id\": [\n" +
            "    \"urn:sfpl:31223092656728\",\n" +
            "    \"urn:sfpl:31223092656751\",\n" +
            "    \"urn:sfpl:31223091222753\",\n" +
            "    \"urn:sfpl:31223092656785\"\n" +
            "  ],\n" +
            "  \"full_title\": \"Alec \\\"The years have pants\\\" : (a life-sized omnibus)\",\n" +
            "  \"lc_classifications\": [\n" +
            "    \"PN6790.A83 C3639 2009\",\n" +
            "    \"PN6738\"\n" +
            "  ],\n" +
            "  \"latest_revision\": 4,\n" +
            "  \"key\": \"/books/OL27117293M\",\n" +
            "  \"authors\": [\n" +
            "    {\n" +
            "      \"key\": \"/authors/OL752714A\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"subtitle\": \"\\\"The years have pants\\\" : (a life-sized omnibus)\",\n" +
            "  \"publish_places\": [\n" +
            "    \"Marietta, GA\"\n" +
            "  ],\n" +
            "  \"subjects\": [\n" +
            "    \"Cartoonists\",\n" +
            "    \"Comic books, strips\",\n" +
            "    \"Artists\"\n" +
            "  ],\n" +
            "  \"isbn_13\": [\n" +
            "    \"9781603090476\",\n" +
            "    \"9781603090254\"\n" +
            "  ],\n" +
            "  \"pagination\": \"638 p.\",\n" +
            "  \"source_records\": [\n" +
            "    \"marc:marc_openlibraries_sanfranciscopubliclibrary/sfpl_chq_2018_12_24_run04.mrc:49156388:3050\",\n" +
            "    \"bwb:9781603090254\",\n" +
            "    \"bwb:9781603090476\"\n" +
            "  ],\n" +
            "  \"title\": \"Alec\",\n" +
            "  \"dewey_decimal_class\": [\n" +
            "    \"741.5/973\"\n" +
            "  ],\n" +
            "  \"number_of_pages\": 638,\n" +
            "  \"created\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2019-07-16T15:11:22.280054\"\n" +
            "  },\n" +
            "  \"languages\": [\n" +
            "    {\n" +
            "      \"key\": \"/languages/eng\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"isbn_10\": [\n" +
            "    \"1603090479\",\n" +
            "    \"1603090258\"\n" +
            "  ],\n" +
            "  \"subject_people\": [\n" +
            "    \"Eddie Campbell (1955-)\"\n" +
            "  ],\n" +
            "  \"publish_country\": \"gau\",\n" +
            "  \"last_modified\": {\n" +
            "    \"type\": \"/type/datetime\",\n" +
            "    \"value\": \"2020-10-09T01:20:24.307407\"\n" +
            "  },\n" +
            "  \"publish_date\": \"2009\",\n" +
            "  \"by_statement\": \"Eddie Campbell\",\n" +
            "  \"oclc_numbers\": [\n" +
            "    \"503594906\"\n" +
            "  ],\n" +
            "  \"works\": [\n" +
            "    {\n" +
            "      \"key\": \"/works/OL19936678W\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"type\": {\n" +
            "    \"key\": \"/type/edition\"\n" +
            "  },\n" +
            "  \"revision\": 4\n" +
            "}"
)