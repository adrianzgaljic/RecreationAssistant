package com.jularic.dominik.recreationassistant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.Manifest;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class GpxActivity extends AppCompatActivity {

    private static final String TAG = "GPX";
    private static final int REQUEST_PERMISSION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpx);

        String path = Environment.getExternalStorageDirectory().toString() + "/temp/mojgpx.gpx";
        //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/temp/RK_gpx _2016-11-21_1926.gpx";
        //String path = "/storage/3664-3066/download/temp/RK_gpx _2016-11-21_1926.gpx";
        String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String targetPath = ExternalStorageDirectoryPath + "/test/RK_gpx _2016-11-21_1926.gpx";

        //File dir = Environment.getExternalStorageDirectory();
        //File yourFile = new File(dir, "/storage/364-3066/mojgpx.gpx");

        Log.d(TAG, targetPath.toString());
        TextView textInfo = (TextView)findViewById(R.id.tv_info);
        String info = "";
        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Uri file = Uri.fromFile(new File(downloadFolder,"mojgpx.gpx" ));
        Log.d(TAG, file.toString());

        File gpxFile = new File(path);
        info += gpxFile.getPath() +"\n\n";

        //Log.d(TAG, info.toString());
        //Log.d(TAG, );

        List<Location> gpxList = decodeGPX(gpxFile);
        //Log.d(TAG, gpxList.toString());

        for(int i = 0; i < gpxList.size(); i++){
            info += ((Location)gpxList.get(i)).getLatitude()
                    + " : "
                    + ((Location)gpxList.get(i)).getLongitude() + "\n";
        }

        textInfo.setText(info);
            }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private List<Location> decodeGPX(File file){
        List<Location> list = new ArrayList<Location>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            FileInputStream fileInputStream = new FileInputStream(file);
            Document document = documentBuilder.parse(fileInputStream);
            Element elementRoot = document.getDocumentElement();

            NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trkpt");

            for(int i = 0; i < nodelist_trkpt.getLength(); i++){

                Node node = nodelist_trkpt.item(i);
                NamedNodeMap attributes = node.getAttributes();

                String newLatitude = attributes.getNamedItem("lat").getTextContent();
                Double newLatitude_double = Double.parseDouble(newLatitude);

                String newLongitude = attributes.getNamedItem("lon").getTextContent();
                Double newLongitude_double = Double.parseDouble(newLongitude);

                String newLocationName = newLatitude + ":" + newLongitude;
                Location newLocation = new Location(newLocationName);
                newLocation.setLatitude(newLatitude_double);
                newLocation.setLongitude(newLongitude_double);

                list.add(newLocation);

            }

            fileInputStream.close();

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }


}
