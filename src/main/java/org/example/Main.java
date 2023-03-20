package org.example;

import com.google.gson.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Введите номер региона");
        String regionId = reader.readLine();
        ArrayList<Realtor> realtors = getAllIds(regionId);
        String baseRealtorUrl = "https://www.cian.ru/agents/";
        StringBuilder builder = new StringBuilder();
        for (Realtor realtor : realtors) {
            String id = realtor.getId();
            String realtorUrl = baseRealtorUrl + id;
            try {
                Document doc = Jsoup.connect(realtorUrl).get();
                parseRealtorName(doc, realtor);
                parseRegistrationDetails(doc, realtor);
                parseContacts(doc, realtor);
                builder.append(realtor);
                builder.append("\n=====================\n");
                writeData(builder.toString(), "realtors" + regionId + ".txt");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                return;
            }
        }


    }

    // FIXME dont parse phone
    // TIP: use HTTP Unit to trigger event on html page
    private static void parseContacts(Document doc, Realtor realtor) {
        Elements elements = doc.select("[data-name=RealtorContacts]");
        for (int i = 0; i < 2; i++) {
            if (!elements.isEmpty()) {
                elements = elements.get(i).children();
            }
        }
        for (Element socialItem : elements) {
            if (socialItem.hasAttr("class")
                    && socialItem.attr("class").contains("socnetwork")) {
                realtor.addContact(socialItem.text());
            }
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static ArrayList<Realtor> getAllIds(String regionId) {
        ArrayList<Realtor> rieltorIds = new ArrayList<>();
        for (int pageNumber = 1; pageNumber < 500; pageNumber++) {
            String url = "https://api.cian.ru/agent-catalog-search/v1/get-realtors/?regionId="
                    + regionId
                    + "&page="
                    + pageNumber;

            try (InputStream stream = new URL(url).openStream()) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                String jsonText = readAll(rd);

                //FIXME deprecated
                Map<String, JsonElement> objects = new JsonParser().parse(jsonText).getAsJsonObject().asMap();
                JsonElement items = objects.get("items");
                JsonArray rieltorArray = items.getAsJsonArray();
                if (rieltorArray.isEmpty()) {
                    break;
                } else {
                    rieltorArray.forEach(x ->
                            rieltorIds.add(new Realtor(x.getAsJsonObject().get("cianUserId").getAsString()))
                    );
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
        return rieltorIds;
    }


    //FIXME close and writing on every operation
    public static void writeData(String realtors, String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write(realtors);
        writer.close();
    }

    public static void parseRealtorName(Document doc, Realtor currentRealtor) {
        Elements nameElements = doc.select("[data-name=Name]");
        for (int i = 0; i < 4; i++) {
            if (!nameElements.isEmpty()) {
                nameElements = nameElements.get(0).children();
            }
        }
        String[] fullName = nameElements
                .text()
                .split(" ");
        currentRealtor.setName(fullName[0]);
        currentRealtor.setSurname(fullName[1]);

    }

    public static void parseRegistrationDetails(Document doc, Realtor currentRealtor) {
        Elements counters = doc.select("[data-name=Counters]").get(0).children();
        List<String> countersList = new ArrayList<>();
        for (Element element : counters) {
            Element element1 = element.children().get(1);
            countersList.add(element1.text());
        }
        currentRealtor.setRegistrationDate(countersList.get(0));
        currentRealtor.setWorkingExperience(countersList.get(1));
        currentRealtor.setObjectCount(countersList.get(2));

    }
}
