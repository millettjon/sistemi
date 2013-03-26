(ns sistemi.color.ral
  :use [color :only [to-rgb]])

;; References:
;; - http://www.ralcolor.com/
;; - https://gist.github.com/lunohodov/1995178

(def colors
  "RAL color table."
  {1000 {:rgb 0xBEBD7F, :de "Grünbeige", :en "Green beige", :fr "Beige vert", :es "Beige verdoso", :it "Beige verdastro"}
   1001 {:rgb 0xC2B078, :de "Beige", :en "Beige", :fr "Beige", :es "Beige", :it "Beige"}
   1002 {:rgb 0xC6A664, :de "Sandgelb", :en "Sand yellow", :fr "Jaune sable", :es "Amarillo arena", :it "Giallo sabbia"}
   1003 {:rgb 0xE5BE01, :de "Signalgelb", :en "Signal yellow", :fr "Jaune de sécurité", :es "Amarillo señales", :it "Giallo segnale"}
   1004 {:rgb 0xCDA434, :de "Goldgelb", :en "Golden yellow", :fr "Jaune or", :es "Amarillo oro", :it "Giallo oro"}
   1005 {:rgb 0xA98307, :de "Honiggelb", :en "Honey yellow", :fr "Jaune miel", :es "Amarillo miel", :it "Giallo miele"}
   1006 {:rgb 0xE4A010, :de "Maisgelb", :en "Maize yellow", :fr "Jaune maïs", :es "Amarillo maiz", :it "Giallo polenta"}
   1007 {:rgb 0xDC9D00, :de "Narzissengelb", :en "Daffodil yellow", :fr "Jaune narcisse", :es "Amarillo narciso", :it "Giallo narciso"}
   1011 {:rgb 0x8A6642, :de "Braunbeige", :en "Brown beige", :fr "Beige brun", :es "Beige pardo", :it "Beige marrone"}
   1012 {:rgb 0xC7B446, :de "Zitronengelb", :en "Lemon yellow", :fr "Jaune citron", :es "Amarillo limón", :it "Giallo limone"}
   1013 {:rgb 0xEAE6CA, :de "Perlweiß", :en "Oyster white", :fr "Blanc perlé", :es "Blanco perla", :it "Bianco perla"}
   1014 {:rgb 0xE1CC4F, :de "Elfenbein", :en "Ivory", :fr "Ivoire", :es "Marfil", :it "Avorio"}
   1015 {:rgb 0xE6D690, :de "Hellelfenbein", :en "Light ivory", :fr "Ivoire clair", :es "Marfil claro", :it "Avorio chiaro"}
   1016 {:rgb 0xEDFF21, :de "Schwefelgelb", :en "Sulfur yellow", :fr "Jaune soufre", :es "Amarillo azufre", :it "Giallo zolfo"}
   1017 {:rgb 0xF5D033, :de "Safrangelb", :en "Saffron yellow", :fr "Jaune safran", :es "Amarillo azafrán", :it "Giallo zafferano"}
   1018 {:rgb 0xF8F32B, :de "Zinkgelb", :en "Zinc yellow", :fr "Jaune zinc", :es "Amarillo de zinc", :it "Giallo zinco"}
   1019 {:rgb 0x9E9764, :de "Graubeige", :en "Grey beige", :fr "Beige gris", :es "Beige agrisado", :it "Beige grigiastro"}
   1020 {:rgb 0x999950, :de "Olivgelb", :en "Olive yellow", :fr "Jaune olive", :es "Amarillo oliva", :it "Giallo olivastro"}
   1021 {:rgb 0xF3DA0B, :de "Rapsgelb", :en "Rape yellow", :fr "Jaune colza", :es "Amarillo colza", :it "Giallo navone"}
   1023 {:rgb 0xFAD201, :de "Verkehrsgelb", :en "Traffic yellow", :fr "Jaune signalisation", :es "Amarillo tráfico", :it "Giallo traffico"}
   1024 {:rgb 0xAEA04B, :de "Ockergelb", :en "Ochre yellow", :fr "Jaune ocre", :es "Amarillo ocre", :it "Giallo ocra"}
   1026 {:rgb 0xFFFF00, :de "Leuchtgelb", :en "Luminous yellow", :fr "Jaune brillant", :es "Amarillo brillante", :it "Giallo brillante"}
   1027 {:rgb 0x9D9101, :de "Currygelb", :en "Curry", :fr "Jaune curry", :es "Amarillo curry", :it "Giallo curry"}
   1028 {:rgb 0xF4A900, :de "Melonengelb", :en "Melon yellow", :fr "Jaune melon", :es "Amarillo melón", :it "Giallo melone"}
   1032 {:rgb 0xD6AE01, :de "Ginstergelb", :en "Broom yellow", :fr "Jaune genêt", :es "Amarillo retama", :it "Giallo scopa"}
   1033 {:rgb 0xF3A505, :de "Dahliengelb", :en "Dahlia yellow", :fr "Jaune dahlia", :es "Amarillo dalia", :it "Giallo dahlien"}
   1034 {:rgb 0xEFA94A, :de "Pastellgelb", :en "Pastel yellow", :fr "Jaune pastel", :es "Amarillo pastel", :it "Giallo pastello"}
   1035 {:rgb 0x6A5D4D, :de "Perlbeige", :en "Pearl beige", :fr "Beige nacré", :es "Beige perlado", :it "Beige perlato"}
   1036 {:rgb 0x705335, :de "Perlgold", :en "Pearl gold", :fr "Or nacré", :es "Oro perlado", :it "Oro perlato"}
   1037 {:rgb 0xF39F18, :de "Sonnengelb", :en "Sun yellow", :fr "Jaune soleil", :es "Amarillo sol", :it "Giallo sole"}
   2000 {:rgb 0xED760E, :de "Gelborange", :en "Yellow orange", :fr "Orangé jaune", :es "Amarillo naranja", :it "Arancio giallastro"}
   2001 {:rgb 0xC93C20, :de "Rotorange", :en "Red orange", :fr "Orangé rouge", :es "Rojo anaranjado", :it "Arancio rossastro"}
   2002 {:rgb 0xCB2821, :de "Blutorange", :en "Vermilion", :fr "Orangé sang", :es "Naranja sanguineo", :it "Arancio sanguigno"}
   2003 {:rgb 0xFF7514, :de "Pastellorange", :en "Pastel orange", :fr "Orangé pastel", :es "Naranja pálido", :it "Arancio pastello"}
   2004 {:rgb 0xF44611, :de "Reinorange", :en "Pure orange", :fr "Orangé pur", :es "Naranja puro", :it "Arancio puro"}
   2005 {:rgb 0xFF2301, :de "Leuchtorange", :en "Luminous orange", :fr "Orangé brillant", :es "Naranja brillante", :it "Arancio brillante"}
   2007 {:rgb 0xFFA420, :de "Leuchthellorange", :en "Luminous bright orange", :fr "Orangé clair rillant", :es "Naranja claro brillante", :it "Arancio chiaro brillante"}
   2009 {:rgb 0xF54021, :de "Verkehrsorange", :en "Traffic orange", :fr "Orangé signalisation", :es "Naranja tráfico", :it "Arancio traffico"}
   2010 {:rgb 0xD84B20, :de "Signalorange", :en "Signal orange", :fr "Orangé de sécurité", :es "Naranja señales", :it "Arancio segnale"}
   2011 {:rgb 0xEC7C26, :de "Tieforange", :en "Deep orange", :fr "Orangé foncé", :es "Naranja intenso", :it "Arancio profondo"}
   2012 {:rgb 0xE55137, :de "Lachsorange", :en "Salmon range", :fr "Orangé saumon", :es "Naranja salmón", :it "Arancio salmone"}
   2013 {:rgb 0xC35831, :de "Perlorange", :en "Pearl orange", :fr "Orangé nacré", :es "Naranja perlado", :it "Arancio perlato"}
   3000 {:rgb 0xAF2B1E, :de "Feuerrot", :en "Flame red", :fr "Rouge feu", :es "Rojo vivo", :it "Rosso fuoco"}
   3001 {:rgb 0xA52019, :de "Signalrot", :en "Signal red", :fr "Rouge de sécurité", :es "Rojo señales", :it "Rosso  segnale"}
   3002 {:rgb 0xA2231D, :de "Karminrot", :en "Carmine red", :fr "Rouge carmin", :es "Rojo carmin", :it "Rosso carminio"}
   3003 {:rgb 0x9B111E, :de "Rubinrot", :en "Ruby red", :fr "Rouge rubis", :es "Rojo rubí", :it "Rosso rubino"}
   3004 {:rgb 0x75151E, :de "Purpurrot", :en "Purple red", :fr "Rouge pourpre", :es "Rojo purpura", :it "Rosso porpora"}
   3005 {:rgb 0x5E2129, :de "Weinrot", :en "Wine red", :fr "Rouge vin", :es "Rojo vino", :it "Rosso vino"}
   3007 {:rgb 0x412227, :de "Schwarzrot", :en "Black red", :fr "Rouge noir", :es "Rojo negruzco", :it "Rosso nerastro"}
   3009 {:rgb 0x642424, :de "Oxidrot", :en "Oxide red", :fr "Rouge oxyde", :es "Rojo óxido", :it "Rosso  ossido"}
   3011 {:rgb 0x781F19, :de "Braunrot", :en "Brown red", :fr "Rouge brun", :es "Rojo pardo", :it "Rosso marrone"}
   3012 {:rgb 0xC1876B, :de "Beigerot", :en "Beige red", :fr "Rouge beige", :es "Rojo beige", :it "Rosso beige"}
   3013 {:rgb 0xA12312, :de "Tomatenrot", :en "Tomato red", :fr "Rouge tomate", :es "Rojo tomate", :it "Rosso pomodoro"}
   3014 {:rgb 0xD36E70, :de "Altrosa", :en "Antique pink", :fr "Vieux rose", :es "Rojo viejo", :it "Rosa antico"}
   3015 {:rgb 0xEA899A, :de "Hellrosa", :en "Light pink", :fr "Rose clair", :es "Rosa claro", :it "Rosa chiaro"}
   3016 {:rgb 0xB32821, :de "Korallenrot", :en "Coral red", :fr "Rouge corail", :es "Rojo coral", :it "Rosso corallo"}
   3017 {:rgb 0xE63244, :de "Rosé", :en "Rose", :fr "Rosé", :es "Rosa", :it "Rosato"}
   3018 {:rgb 0xD53032, :de "Erdbeerrot", :en "Strawberry red", :fr "Rouge fraise", :es "Rojo fresa", :it "Rosso fragola"}
   3020 {:rgb 0xCC0605, :de "Verkehrsrot", :en "Traffic red", :fr "Rouge signalisation", :es "Rojo tráfico", :it "Rosso traffico"}
   3022 {:rgb 0xD95030, :de "Lachsrot", :en "Salmon pink", :fr "Rouge saumon", :es "Rojo salmón", :it "Rosso salmone"}
   3024 {:rgb 0xF80000, :de "Leuchtrot", :en "Luminous red", :fr "Rouge brillant", :es "Rojo brillante", :it "Rosso brillante"}
   3026 {:rgb 0xFE0000, :de "Leuchthellrot", :en "Luminous bright red", :fr "Rouge clair brillant", :es "Rojo claro brillante", :it "Rosso chiaro brillante"}
   3027 {:rgb 0xC51D34, :de "Himbeerrot", :en "Raspberry red", :fr "Rouge framboise", :es "Rojo frambuesa", :it "Rosso lampone"}
   3028 {:rgb 0xCB3234, :de "Reinrot", :en "Pure  red", :fr "Rouge puro", :es "Rojo puro", :it "Rosso puro"}
   3031 {:rgb 0xB32428, :de "Orientrot", :en "Orient red", :fr "Rouge oriental", :es "Rojo oriente", :it "Rosso oriente"}
   3032 {:rgb 0x721422, :de "Perlrubinrot", :en "Pearl ruby red", :fr "Rouge rubis nacré", :es "Rojo rubí perlado", :it "Rosso rubino perlato"}
   3033 {:rgb 0xB44C43, :de "Perlrosa", :en "Pearl pink", :fr "Rose nacré", :es "Rosa perlado", :it "Rosa perlato"}
   4001 {:rgb 0x6D3F5B, :de "Rotlila", :en "Red lilac", :fr "Lilas rouge", :es "Rojo lila", :it "Lilla rossastro"}
   4002 {:rgb 0x922B3E, :de "Rotviolett", :en "Red violet", :fr "Violet rouge", :es "Rojo violeta", :it "Viola rossastro"}
   4003 {:rgb 0xDE4C8A, :de "Erikaviolett", :en "Heather violet", :fr "Violet bruyère", :es "Violeta érica", :it "Viola erica"}
   4004 {:rgb 0x641C34, :de "Bordeauxviolett", :en "Claret violet", :fr "Violet bordeaux", :es "Burdeos", :it "Viola bordeaux"}
   4005 {:rgb 0x6C4675, :de "Blaulila", :en "Blue lilac", :fr "Lilas bleu", :es "Lila azulado", :it "Lilla bluastro"}
   4006 {:rgb 0xA03472, :de "Verkehrspurpur", :en "Traffic purple", :fr "Pourpre signalisation", :es "Púrpurá tráfico", :it "Porpora traffico"}
   4007 {:rgb 0x4A192C, :de "Purpurviolett", :en "Purple violet", :fr "Violet pourpre", :es "Violeta púrpura", :it "Porpora violetto"}
   4008 {:rgb 0x924E7D, :de "Signalviolett", :en "Signal violet", :fr "Violet de sécurité", :es "Violeta señales", :it "Violetto segnale"}
   4009 {:rgb 0xA18594, :de "Pastellviolett", :en "Pastel violet", :fr "Violet pastel", :es "Violeta pastel", :it "Violetto pastello"}
   4010 {:rgb 0xCF3476, :de "Telemagenta", :en "Telemagenta", :fr "Telemagenta", :es "Magenta tele", :it "Tele Magenta"}
   4011 {:rgb 0x8673A1, :de "Perlviolett", :en "Pearl violet", :fr "Violet nacré", :es "Violeta perlado", :it "Violetto perlato"}
   4012 {:rgb 0x6C6874, :de "Perlbrombeer", :en "Pearl black berry", :fr "Mûre nacré", :es "Morado perlado", :it "Mora perlato"}
   5000 {:rgb 0x354D73, :de "Violettblau", :en "Violet blue", :fr "Bleu violet", :es "Azul violeta", :it "Blu violaceo"}
   5001 {:rgb 0x1F3438, :de "Grünblau", :en "Green blue", :fr "Bleu vert", :es "Azul verdoso", :it "Blu verdastro"}
   5002 {:rgb 0x20214F, :de "Ultramarinblau", :en "Ultramarine blue", :fr "Bleu outremer", :es "Azul ultramar", :it "Blu oltremare"}
   5003 {:rgb 0x1D1E33, :de "Saphirblau", :en "Saphire blue", :fr "Bleu saphir", :es "Azul zafiro", :it "Blu zaffiro"}
   5004 {:rgb 0x18171C, :de "Schwarzblau", :en "Black blue", :fr "Bleu noir", :es "Azul negruzco", :it "Blu nerastro"}
   5005 {:rgb 0x1E2460, :de "Signalblau", :en "Signal blue", :fr "Bleu de sécurité", :es "Azul señales", :it "Blu segnale"}
   5007 {:rgb 0x3E5F8A, :de "Brillantblau", :en "Brillant blue", :fr "Bleu brillant", :es "Azul brillante", :it "Blu brillante"}
   5008 {:rgb 0x26252D, :de "Graublau", :en "Grey blue", :fr "Bleu gris", :es "Azul grisáceo", :it "Blu grigiastro"}
   5009 {:rgb 0x025669, :de "Azurblau", :en "Azure blue", :fr "Bleu azur", :es "Azul azur", :it "Blu  azzurro"}
   5010 {:rgb 0x0E294B, :de "Enzianblau", :en "Gentian blue", :fr "Bleu gentiane", :es "Azul genciana", :it "Blu  genziana"}
   5011 {:rgb 0x231A24, :de "Stahlblau", :en "Steel blue", :fr "Bleu acier", :es "Azul acero", :it "Blu acciaio"}
   5012 {:rgb 0x3B83BD, :de "Lichtblau", :en "Light blue", :fr "Bleu clair", :es "Azul luminoso", :it "Blu luce"}
   5013 {:rgb 0x1E213D, :de "Kobaltblau", :en "Cobalt blue", :fr "Bleu cobalt", :es "Azul cobalto", :it "Blu cobalto"}
   5014 {:rgb 0x606E8C, :de "Taubenblau", :en "Pigeon blue", :fr "Bleu pigeon", :es "Azul olombino", :it "Blu colomba"}
   5015 {:rgb 0x2271B3, :de "Himmelblau", :en "Sky blue", :fr "Bleu ciel", :es "Azul celeste", :it "Blu cielo"}
   5017 {:rgb 0x063971, :de "Verkehrsblau", :en "Traffic blue", :fr "Bleu signalisation", :es "Azul tráfico", :it "Blu traffico"}
   5018 {:rgb 0x3F888F, :de "Türkisblau", :en "Turquoise blue", :fr "Bleu turquoise", :es "Azul turquesa", :it "Blu turchese"}
   5019 {:rgb 0x1B5583, :de "Capriblau", :en "Capri blue", :fr "Bleu capri", :es "Azul capri", :it "Blu capri"}
   5020 {:rgb 0x1D334A, :de "Ozeanblau", :en "Ocean blue", :fr "Bleu océan", :es "Azul oceano", :it "Blu oceano"}
   5021 {:rgb 0x256D7B, :de "Wasserblau", :en "Water blue", :fr "Bleu d’eau", :es "Azul agua", :it "Blu acqua"}
   5022 {:rgb 0x252850, :de "Nachtblau", :en "Night blue", :fr "Bleu nocturne", :es "Azul noche", :it "Blu notte"}
   5023 {:rgb 0x49678D, :de "Fernblau", :en "Distant blue", :fr "Bleu distant", :es "Azul lejanía", :it "Blu distante"}
   5024 {:rgb 0x5D9B9B, :de "Pastellblau", :en "Pastel blue", :fr "Bleu pastel", :es "Azul pastel", :it "Blu pastello"}
   5025 {:rgb 0x2A6478, :de "Perlenzian", :en "Pearl gentian blue", :fr "Gentiane nacré", :es "Gencian perlado", :it "Blu genziana perlato"}
   5026 {:rgb 0x102C54, :de "Perlnachtblau", :en "Pearl night blue", :fr "Bleu nuit nacré", :es "Azul noche perlado", :it "Blu notte perlato"}
   6000 {:rgb 0x316650, :de "Patinagrün", :en "Patina green", :fr "Vert patine", :es "Verde patina", :it "Verde patina"}
   6001 {:rgb 0x287233, :de "Smaragdgrün", :en "Emerald green", :fr "Vert émeraude", :es "Verde esmeralda", :it "Verde smeraldo"}
   6002 {:rgb 0x2D572C, :de "Laubgrün", :en "Leaf green", :fr "Vert feuillage", :es "Verde hoja", :it "Verde foglia"}
   6003 {:rgb 0x424632, :de "Olivgrün", :en "Olive green", :fr "Vert olive", :es "Verde oliva", :it "Verde oliva"}
   6004 {:rgb 0x1F3A3D, :de "Blaugrün", :en "Blue green", :fr "Vert bleu", :es "Verde azulado", :it "Verde bluastro"}
   6005 {:rgb 0x2F4538, :de "Moosgrün", :en "Moss green", :fr "Vert mousse", :es "Verde musgo", :it "Verde muschio"}
   6006 {:rgb 0x3E3B32, :de "Grauoliv", :en "Grey olive", :fr "Olive gris", :es "Oliva grisáceo", :it "Oliva grigiastro"}
   6007 {:rgb 0x343B29, :de "Flaschengrün", :en "Bottle green", :fr "Vert bouteille", :es "Verde botella", :it "Verde bottiglia"}
   6008 {:rgb 0x39352A, :de "Braungrün", :en "Brown green", :fr "Vert brun", :es "Verde parduzco", :it "Verde brunastro"}
   6009 {:rgb 0x31372B, :de "Tannengrün", :en "Fir green", :fr "Vert sapin", :es "Verde abeto", :it "Verde abete"}
   6010 {:rgb 0x35682D, :de "Grasgrün", :en "Grass green", :fr "Vert herbe", :es "Verde hierba", :it "Verde erba"}
   6011 {:rgb 0x587246, :de "Resedagrün", :en "Reseda green", :fr "Vert réséda", :es "Verde reseda", :it "Verde reseda"}
   6012 {:rgb 0x343E40, :de "Schwarzgrün", :en "Black green", :fr "Vert noir", :es "Verde negruzco", :it "Verde nerastro"}
   6013 {:rgb 0x6C7156, :de "Schilfgrün", :en "Reed green", :fr "Vert jonc", :es "Verde caña", :it "Verde canna"}
   6014 {:rgb 0x47402E, :de "Gelboliv", :en "Yellow olive", :fr "Olive jaune", :es "Amarillo oliva", :it "Oliva giallastro"}
   6015 {:rgb 0x3B3C36, :de "Schwarzoliv", :en "Black olive", :fr "Olive noir", :es "Oliva negruzco", :it "Oliva nerastro"}
   6016 {:rgb 0x1E5945, :de "Türkisgrün", :en "Turquoise green", :fr "Vert turquoise", :es "Verde turquesa", :it "Verde turchese"}
   6017 {:rgb 0x4C9141, :de "Maigrün", :en "May green", :fr "Vert mai", :es "Verde mayo", :it "Verde maggio"}
   6018 {:rgb 0x57A639, :de "Gelbgrün", :en "Yellow green", :fr "Vert jaune", :es "Verde amarillento", :it "Verde giallastro"}
   6019 {:rgb 0xBDECB6, :de "Weißgrün", :en "Pastel green", :fr "Vert blanc", :es "Verde lanquecino", :it "Verde biancastro"}
   6020 {:rgb 0x2E3A23, :de "Chromoxidgrün", :en "Chrome green", :fr "Vert oxyde chromique", :es "Verde cromo", :it "Verde cromo"}
   6021 {:rgb 0x89AC76, :de "Blassgrün", :en "Pale green", :fr "Vert pâle", :es "Verde pálido", :it "Verde pallido"}
   6022 {:rgb 0x25221B, :de "Braunoliv", :en "Olive drab", :fr "Olive brun", :es "Oliva parduzco", :it "Oliva brunastro"}
   6024 {:rgb 0x308446, :de "Verkehrsgrün", :en "Traffic green", :fr "Vert signalisation", :es "Verde tráfico", :it "Verde traffico"}
   6025 {:rgb 0x3D642D, :de "Farngrün", :en "Fern green", :fr "Vert fougère", :es "Verde helecho", :it "Verde felce"}
   6026 {:rgb 0x015D52, :de "Opalgrün", :en "Opal green", :fr "Vert opale", :es "Verde opalo", :it "Verde opale"}
   6027 {:rgb 0x84C3BE, :de "Lichtgrün", :en "Light green", :fr "Vert clair", :es "Verde luminoso", :it "Verde chiaro"}
   6028 {:rgb 0x2C5545, :de "Kieferngrün", :en "Pine green", :fr "Vert pin", :es "Verde pino", :it "Verde pino"}
   6029 {:rgb 0x20603D, :de "Minzgrün", :en "Mint green", :fr "Vert menthe", :es "Verde menta", :it "Verde menta"}
   6032 {:rgb 0x317F43, :de "Signalgrün", :en "Signal green", :fr "Vert de sécurité", :es "Verde señales", :it "Verde segnale"}
   6033 {:rgb 0x497E76, :de "Minttürkis", :en "Mint turquoise", :fr "Turquoise menthe", :es "Turquesa menta", :it "Turchese menta"}
   6034 {:rgb 0x7FB5B5, :de "Pastelltürkis", :en "Pastel turquoise", :fr "Turquoise pastel", :es "Turquesa pastel", :it "Turchese pastello"}
   6035 {:rgb 0x1C542D, :de "Perlgrün", :en "Pearl green", :fr "Vert nacré", :es "Verde perlado", :it "Verde perlato"}
   6036 {:rgb 0x193737, :de "Perlopalgrün", :en "Pearl opal green", :fr "Vert opal nacré", :es "Verde ópalo perlado", :it "Verde opalo perlato"}
   6037 {:rgb 0x008F39, :de "Reingrün", :en "Pure green", :fr "Vert pur", :es "Verde puro", :it "Verde puro"}
   6038 {:rgb 0x00BB2D, :de "Leuchtgrün", :en "Luminous green", :fr "Vert brillant", :es "Verde brillante", :it "Verde brillante"}
   7000 {:rgb 0x78858B, :de "Fehgrau", :en "Squirrel grey", :fr "Gris petit-gris", :es "Gris ardilla", :it "Grigio vaio"}
   7001 {:rgb 0x8A9597, :de "Silbergrau", :en "Silver grey", :fr "Gris argent", :es "Gris plata", :it "Grigio argento"}
   7002 {:rgb 0x7E7B52, :de "Olivgrau", :en "Olive grey", :fr "Gris olive", :es "Gris oliva", :it "Grigio olivastro"}
   7003 {:rgb 0x6C7059, :de "Moosgrau", :en "Moss grey", :fr "Gris mousse", :es "Gris musgo", :it "Grigio muschio"}
   7004 {:rgb 0x969992, :de "Signalgrau", :en "Signal grey", :fr "Gris de sécurité", :es "Gris señales", :it "Grigio segnale"}
   7005 {:rgb 0x646B63, :de "Mausgrau", :en "Mouse grey", :fr "Gris souris", :es "Gris ratón", :it "Grigio topo"}
   7006 {:rgb 0x6D6552, :de "Beigegrau", :en "Beige grey", :fr "Gris beige", :es "Gris beige", :it "Grigio beige"}
   7008 {:rgb 0x6A5F31, :de "Khakigrau", :en "Khaki grey", :fr "Gris kaki", :es "Gris caqui", :it "Grigio kaki"}
   7009 {:rgb 0x4D5645, :de "Grüngrau", :en "Green grey", :fr "Gris vert", :es "Gris verdoso", :it "Grigio verdastro"}
   7010 {:rgb 0x4C514A, :de "Zeltgrau", :en "Tarpaulin grey", :fr "Gris tente", :es "Gris lona", :it "Grigio tenda"}
   7011 {:rgb 0x434B4D, :de "Eisengrau", :en "Iron grey", :fr "Gris fer", :es "Gris hierro", :it "Grigio ferro"}
   7012 {:rgb 0x4E5754, :de "Basaltgrau", :en "Basalt grey", :fr "Gris basalte", :es "Gris basalto", :it "Grigio basalto"}
   7013 {:rgb 0x464531, :de "Braungrau", :en "Brown grey", :fr "Gris brun", :es "Gris parduzco", :it "Grigio brunastro"}
   7015 {:rgb 0x434750, :de "Schiefergrau", :en "Slate grey", :fr "Gris ardoise", :es "Gris pizarra", :it "Grigio ardesia"}
   7016 {:rgb 0x293133, :de "Anthrazitgrau", :en "Anthracite grey", :fr "Gris anthracite", :es "Gris antracita", :it "Grigio antracite"}
   7021 {:rgb 0x23282B, :de "Schwarzgrau", :en "Black grey", :fr "Gris noir", :es "Gris negruzco", :it "Grigio nerastro"}
   7022 {:rgb 0x332F2C, :de "Umbragrau", :en "Umbra grey", :fr "Gris terre d’ombre", :es "Gris sombra", :it "Grigio ombra"}
   7023 {:rgb 0x686C5E, :de "Betongrau", :en "Concrete grey", :fr "Gris béton", :es "Gris hormigón", :it "Grigio calcestruzzo"}
   7024 {:rgb 0x474A51, :de "Graphitgrau", :en "Graphite grey", :fr "Gris graphite", :es "Gris grafita", :it "Grigio grafite"}
   7026 {:rgb 0x2F353B, :de "Granitgrau", :en "Granite grey", :fr "Gris granit", :es "Gris granito", :it "Grigio granito"}
   7030 {:rgb 0x8B8C7A, :de "Steingrau", :en "Stone grey", :fr "Gris pierre", :es "Gris piedra", :it "Grigio pietra"}
   7031 {:rgb 0x474B4E, :de "Blaugrau", :en "Blue grey", :fr "Gris bleu", :es "Gris azulado", :it "Grigio bluastro"}
   7032 {:rgb 0xB8B799, :de "Kieselgrau", :en "Pebble grey", :fr "Gris silex", :es "Gris guijarro", :it "Grigio ghiaia"}
   7033 {:rgb 0x7D8471, :de "Zementgrau", :en "Cement grey", :fr "Gris ciment", :es "Gris cemento", :it "Grigio cemento"}
   7034 {:rgb 0x8F8B66, :de "Gelbgrau", :en "Yellow grey", :fr "Gris jaune", :es "Gris amarillento", :it "Grigio giallastro"}
   7035 {:rgb 0xD7D7D7, :de "Lichtgrau", :en "Light grey", :fr "Gris clair", :es "Gris luminoso", :it "Grigio luce"}
   7036 {:rgb 0x7F7679, :de "Platingrau", :en "Platinum grey", :fr "Gris platine", :es "Gris platino", :it "Grigio platino"}
   7037 {:rgb 0x7D7F7D, :de "Staubgrau", :en "Dusty grey", :fr "Gris poussière", :es "Gris polvo", :it "Grigio polvere"}
   7038 {:rgb 0xB5B8B1, :de "Achatgrau", :en "Agate grey", :fr "Gris agate", :es "Gris ágata", :it "Grigio agata"}
   7039 {:rgb 0x6C6960, :de "Quarzgrau", :en "Quartz grey", :fr "Gris quartz", :es "Gris cuarzo", :it "Grigio quarzo"}
   7040 {:rgb 0x9DA1AA, :de "Fenstergrau", :en "Window grey", :fr "Gris fenêtre", :es "Gris ventana", :it "Grigio finestra"}
   7042 {:rgb 0x8D948D, :de "Verkehrsgrau A", :en "Traffic grey A", :fr "Gris signalisation A", :es "Gris tráfico A", :it "Grigio traffico A"}
   7043 {:rgb 0x4E5452, :de "Verkehrsgrau B", :en "Traffic grey B", :fr "Gris signalisation B", :es "Gris tráfico B", :it "Grigio traffico B"}
   7044 {:rgb 0xCAC4B0, :de "Seidengrau", :en "Silk grey", :fr "Gris soie", :es "Gris seda", :it "Grigio seta"}
   7045 {:rgb 0x909090, :de "Telegrau 1", :en "Telegrey 1", :fr "Telegris 1", :es "Gris tele 1", :it "Tele grigio 1"}
   7046 {:rgb 0x82898F, :de "Telegrau 2", :en "Telegrey 2", :fr "Telegris 2", :es "Gris tele 2", :it "Tele grigio 2"}
   7047 {:rgb 0xD0D0D0, :de "Telegrau 4", :en "Telegrey 4", :fr "Telegris 4", :es "Gris tele 4", :it "Tele grigio 4"}
   7048 {:rgb 0x898176, :de "Perlmausgrau", :en "Pearl mouse grey", :fr "Gris souris nacré", :es "Gris musgo perlado", :it "Grigio topo perlato"}
   8000 {:rgb 0x826C34, :de "Grünbraun", :en "Green brown", :fr "Brun vert", :es "Pardo verdoso", :it "Marrone verdastro"}
   8001 {:rgb 0x955F20, :de "Ockerbraun", :en "Ochre brown", :fr "Brun terre de Sienne", :es "Pardo ocre", :it "Marrone ocra"}
   8002 {:rgb 0x6C3B2A, :de "Signalbraun", :en "Signal brown", :fr "Brun de sécurité", :es "Marrón señales", :it "Marrone segnale"}
   8003 {:rgb 0x734222, :de "Lehmbraun", :en "Clay brown", :fr "Brun argile", :es "Pardo arcilla", :it "Marrone fango"}
   8004 {:rgb 0x8E402A, :de "Kupferbraun", :en "Copper brown", :fr "Brun cuivré", :es "Pardo cobre", :it "Marrone rame"}
   8007 {:rgb 0x59351F, :de "Rehbraun", :en "Fawn brown", :fr "Brun fauve", :es "Pardo corzo", :it "Marrone capriolo"}
   8008 {:rgb 0x6F4F28, :de "Olivbraun", :en "Olive brown", :fr "Brun olive", :es "Pardo oliva", :it "Marrone oliva"}
   8011 {:rgb 0x5B3A29, :de "Nussbraun", :en "Nut brown", :fr "Brun noisette", :es "Pardo nuez", :it "Marrone noce"}
   8012 {:rgb 0x592321, :de "Rotbraun", :en "Red brown", :fr "Brun rouge", :es "Pardo rojo", :it "Marrone rossiccio"}
   8014 {:rgb 0x382C1E, :de "Sepiabraun", :en "Sepia brown", :fr "Brun sépia", :es "Sepia", :it "Marrone seppia"}
   8015 {:rgb 0x633A34, :de "Kastanienbraun", :en "Chestnut brown", :fr "Marron", :es "Castaño", :it "Marrone castagna"}
   8016 {:rgb 0x4C2F27, :de "Mahagonibraun", :en "Mahogany brown", :fr "Brun acajou", :es "Caoba", :it "Marrone mogano"}
   8017 {:rgb 0x45322E, :de "Schokoladen-braun", :en "Chocolate brown", :fr "Brun chocolat", :es "Chocolate", :it "Marrone cioccolata"}
   8019 {:rgb 0x403A3A, :de "Graubraun", :en "Grey brown", :fr "Brun gris", :es "Pardo grisáceo", :it "Marrone grigiastro"}
   8022 {:rgb 0x212121, :de "Schwarzbraun", :en "Black brown", :fr "Brun noir", :es "Pardo negruzco", :it "Marrone nerastro"}
   8023 {:rgb 0xA65E2E, :de "Orangebraun", :en "Orange brown", :fr "Brun orangé", :es "Pardo anaranjado", :it "Marrone arancio"}
   8024 {:rgb 0x79553D, :de "Beigebraun", :en "Beige brown", :fr "Brun beige", :es "Pardo beige", :it "Marrone beige"}
   8025 {:rgb 0x755C48, :de "Blassbraun", :en "Pale brown", :fr "Brun pâle", :es "Pardo pálido", :it "Marrone pallido"}
   8028 {:rgb 0x4E3B31, :de "Terrabraun", :en "Terra brown", :fr "Brun terre", :es "Marrón tierra", :it "Marrone terra"}
   8029 {:rgb 0x763C28, :de "Perlkupfer", :en "Pearl copper", :fr "Cuivre nacré", :es "Cobre perlado", :it "Rame perlato"}
   9001 {:rgb 0xFDF4E3, :de "Cremeweiß", :en "Cream", :fr "Blanc crème", :es "Blanco crema", :it "Bianco crema"}
   9002 {:rgb 0xE7EBDA, :de "Grauweiß", :en "Grey white", :fr "Blanc gris", :es "Blanco grisáceo", :it "Bianco grigiastro"}
   9003 {:rgb 0xF4F4F4, :de "Signalweiß", :en "Signal white", :fr "Blanc de sécurité", :es "Blanco señales", :it "Bianco segnale"}
   9004 {:rgb 0x282828, :de "Signalschwarz", :en "Signal black", :fr "Noir de sécurité", :es "Negro señales", :it "Nero segnale"}
   9005 {:rgb 0x0A0A0A, :de "Tiefschwarz", :en "Jet black", :fr "Noir foncé", :es "Negro intenso", :it "Nero intenso"}
   9006 {:rgb 0xA5A5A5, :de "Weißaluminium", :en "White aluminium", :fr "Aluminium blanc", :es "Aluminio blanco", :it "Aluminio brillante"}
   9007 {:rgb 0x8F8F8F, :de "Graualuminium", :en "Grey aluminium", :fr "Aluminium gris", :es "Aluminio gris", :it "Aluminio grigiastro"}
   9010 {:rgb 0xFFFFFF, :de "Reinweiß", :en "Pure white", :fr "Blanc pur", :es "Blanco puro", :it "Bianco puro"}
   9011 {:rgb 0x1C1C1C, :de "Graphitschwarz", :en "Graphite black", :fr "Noir graphite", :es "Negro grafito", :it "Nero grafite"}
   9016 {:rgb 0xF6F6F6, :de "Verkehrsweiß", :en "Traffic white", :fr "Blanc signalisation", :es "Blanco tráfico", :it "Bianco traffico"}
   9017 {:rgb 0x1E1E1E, :de "Verkehrs-schwarz", :en "Traffic black", :fr "Noir signalisation", :es "Negro tráfico", :it "Nero traffico"}
   9018 {:rgb 0xD7D7D7, :de "Papyrusweiß", :en "Papyrus white", :fr "Blanc papyrus", :es "Blanco papiro", :it "Bianco papiro"}
   9022 {:rgb 0x9C9C9C, :de "Perlhellgrau", :en "Pearl light grey", :fr "Gris clair nacré", :es "Gris claro perlado", :it "Grigio chiaro perlato"}
   9023 {:rgb 0x828282, :de "Perldunkelgrau", :en "Pearl dark grey", :fr "Gris fonçé nacré", :es "Gris oscuro perlado", :it "Grigio scuro perlato"}
   })

;; Assumes 'color' to have a :type and :value  {:type :ral, :value 1001}
(defmethod to-rgb :ral [color] (get-in colors [(get color :value) :rgb]))