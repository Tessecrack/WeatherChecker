import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Parser
{
    public static void main(String[] args) throws Exception
    {
        Document webPage = getWebDocument();
        if (webPage == null)
        {
            System.out.println("Не найдена страница с погодой");
            return;
        }

        Element heading = webPage.select("h1[class=heading]").first();

        System.out.println(heading.text());

        Element tableOfWeather = GetTableOfWeather(webPage);
        if (tableOfWeather == null)
        {
            System.out.println("Не найден элемент таблицы с погодой");
            return;
        }

        Elements blocksOfTable = GetBlocksOfTable(tableOfWeather);
        System.out.println(blocksOfTable.size());
        if (blocksOfTable == null)
        {
            System.out.println("Не найдена информация о погоде в блоках таблицы");
            return;
        }
        System.out.println(GetWeatherFromBlocks(blocksOfTable));
    }

    public static Document getWebDocument()
    {
        String nameUrl = "https://pogoda.mail.ru/prognoz/rostov-na-donu/14dney/";
        URL webUrl = null;
        Document webPage = null;
        try
        {
            webUrl = new URL(nameUrl);
        }
        catch (MalformedURLException exception)
        {
            System.out.println(exception.toString());
        }
        try
        {
            webPage = Jsoup.parse(webUrl, 3000);
        }
        catch (IOException exception)
        {
            System.out.println(exception.toString());
        }
        return webPage;
    }

    public static Element GetTableOfWeather(Document webPage)
    {
        Element tableOfWeather = null;

        tableOfWeather = webPage.select("div[class=g-layout layout js-module]").first();

        return tableOfWeather;
    }

    public static Elements GetBlocksOfTable(Element tableOfWeather)
    {
        Elements elementsOfTable = null;

        elementsOfTable = tableOfWeather.select("div[class=block]");

        return elementsOfTable;
    }

    public static String GetWeatherFromBlocks(Elements blocksOfTable)
    {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < blocksOfTable.size(); i++)
        {
            Element block = blocksOfTable.get(i);

            var titleUsually = block.select("div[class=heading heading_minor heading_line]");
            var titleRed = block.select("div[class=heading heading_minor heading_line text-red]");

            var titleUsuallyText = titleUsually.text();
            var titleRedText = titleRed.text();

            var titleOfTableDayPeriods = block.select("div[class=cols__column__item cols__column__item_2-1 cols__column__item_2-1_ie8]");
            StringBuilder strBuilderDays = new StringBuilder();

            if (titleUsuallyText != "")
            {
                result.append(String.format("%s+\n", titleUsuallyText));

                strBuilderDays.append(GetDayPeriods(titleOfTableDayPeriods));
            }

            if (titleRedText != "")
            {
                result.append(String.format("%s+\n", titleRedText));

                strBuilderDays.append(GetDayPeriods(titleOfTableDayPeriods));
            }
            result.append(strBuilderDays);
        }
        return result.toString();
    }

    public static String GetDayPeriods(Elements tableOfPeriods)
    {
        StringBuilder result = new StringBuilder();

        Elements dayPeriods = tableOfPeriods.select("div[class=day day_period]");

        for (int i = 0; i< dayPeriods.size(); i++)
        {
            var blockPeriod = dayPeriods.get(i).select("div[class=day__date]").first();
            var blockTemperature = dayPeriods.get(i).select("div[class=day__temperature]").first();

            //result.append(blockPeriod.text() + " : " + blockTemperature.text());
            result.append(String.format("%s : %s", blockPeriod.text(), blockTemperature.text()));
        }
        result.append("\r\n");
        return result.toString();
    }
}
