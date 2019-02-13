package com.example.convertplisttojson;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ParseFileHelper {
    OnParseCompleteLister lister;
    Context cn;
    private ParseFileHelper(){}

    public ParseFileHelper(Context cn, OnParseCompleteLister lister){
        this.cn=cn;
        this.lister=lister;
    }

    public void startParsing(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                XmlPullParserFactory factory;
                try {
                    is = cn.getAssets().open("r.plist");
                    factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware( true );
                    XmlPullParser parser = factory.newPullParser();
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

                    InputStreamReader inputStreamReader = new InputStreamReader(is);

                    parser.setInput(inputStreamReader);
                    processParsing(parser);
                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                    lister.onComplete(false,null);
                }
            }
        }).start();


    }

    Stack<Items> stack = new Stack<Items>();
    JSONObject jsonObj;
    private void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException{
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String eltName;
            switch (eventType) {
                case XmlPullParser.START_TAG:

                    eltName=parser.getName();
                    Log.d("elmname-start",String.valueOf(eltName));
                    if(eltName.equals(Constant.XmlTag.DICT)){
                        if(jsonObj==null) {
                            jsonObj = new JSONObject();
                        }else{

                        }
                    }else if(eltName.equals(Constant.XmlTag.ARRAY)){
                        Items items= stack.peek();
                        if(items.type==Constant.XmlTag.KEY){
                            JSONArray arr = new JSONArray();
                            Items obj = new Items(Constant.XmlTag.ARRAY,items.value,jsonObj);
                            stack.pop();
                            stack.push(obj);
                        }
                    }else if(eltName.equals(Constant.XmlTag.KEY)){
                        Items obj = new Items(Constant.XmlTag.KEY,parser.getText(),null);
                        stack.push(obj);
                    }else if(eltName.equals(Constant.XmlTag.STRING)){
                        Items items= stack.peek();
                        stack.pop();
                        if(items.type==Constant.XmlTag.KEY){

                            Items obj = new Items(Constant.XmlTag.ARRAY,items.value,jsonObj);
                            stack.pop();
                            stack.push(obj);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:

                    eltName=parser.getName();
                    Log.d("elmname-end",String.valueOf(eltName));
                    break;
            }
            eventType = parser.next();
            if(eventType == XmlPullParser.END_DOCUMENT){
                Log.d("sds","Sds");
            }
        }
    }

    class Items{
        public String type,value;
        public Object data;

        public Items(String type, String value, Object data) {
            this.type = type;
            this.value = value;
            this.data = data;
        }
    }

}
