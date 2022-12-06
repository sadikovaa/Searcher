package com.alsu.plugin;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class TranslateAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = editor.getCaretModel();
        String query = caretModel.getCurrentCaret().getSelectedText();

        if (query != null) {
            //query = query.replace(" ", "%20");
            //BrowserUtil.browse("https://translate.yandex.ru/?from=tableau_yabro&source_lang=en&target_lang=ru&text=" + query);

            URIBuilder builder;
            try {
                builder = new URIBuilder("https://api.mymemory.translated.net/get");
                builder.setParameter("q", query).setParameter("langpair", "en|ru");
                System.out.println(builder.build().toString());
                HttpGet get = new HttpGet(builder.build());

                HttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(get);
                String JSONString = EntityUtils.toString(response.getEntity(),
                        "UTF-8");
                System.out.println("JSONString");
                System.out.println(JSONString);
                String[] answer = JSONString.split("\"");
                String translated = "";
                for (String answerPart : answer) {
                    if (answerPart.contains("\\u")) {
                        translated = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(answerPart);
                        break;
                    }
                }

                if (translated.isEmpty())
                    Messages.showMessageDialog(
                            project, "Перевод не доступен.", "Error", Messages.getErrorIcon());

                Messages.showMessageDialog(
                        project, translated, "Your translation", Messages.getInformationIcon());

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
}
