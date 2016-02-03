package flq.projectbooks.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.common.io.ByteStreams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import flq.projectbooks.Book;
import flq.projectbooks.libraries.BookLibrary;

/**
 * Created by doublet on 05/11/15.
 */
public class GetBookInfoAmazonAPI extends GetBookInfo {

    public GetBookInfoAmazonAPI(Context context){
        super(context);
    }

    private static final String UTF8_CHARSET = "UTF-8";
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static final String REQUEST_URI = "/onca/xml";
    private static final String REQUEST_METHOD = "GET";

    // use xml-uk.amznxslt.com for xslt requests, or ecs.amazonaws.co.uk for others
    private String endpoint = "webservices.amazon.com"; // must be lowercase

    // change this so reads from properties file
    private String awsAccessKeyId = "";
    private String awsSecretKey = "";

    private SecretKeySpec secretKeySpec = null;
    private Mac mac = null;

    @Override
    protected Book doInBackground(String... isbns) {
        // Stop if cancelled
        if (isCancelled()) {
            return null;
        }


        try {
            initAPI();

            Map<String, String> myparams = new HashMap<String, String>();
            myparams.put("Service", "AWSECommerceService");
            myparams.put("Operation", "ItemLookup");
            myparams.put("ResponseGroup", "Large");
            myparams.put("SearchIndex", "All");
            myparams.put("IdType", "ISBN");
            myparams.put("AssociateTag", "biblioid-21");
           // myparams.put("ItemId",  "2070615367");//
            //myparams.put("ItemId",  "0449001245");
            myparams.put("ItemId",  isbns[0]);
            String apiUrlString = sign(myparams);

            HttpURLConnection connection = null;
            URL url = new URL(apiUrlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000); // 5 seconds
            connection.setConnectTimeout(5000); // 5 seconds

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                Log.w(getClass().getName(), "AmazonAPI request failed. Response Code: " + responseCode);
                connection.disconnect();
                return null;
            }

            // Read data from response.
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null){
                builder.append(line);
                line = responseReader.readLine();
            }
            String responseString = builder.toString();
            Log.d(getClass().getName(), "Response String: " + responseString);

            // Close connection and return response code.
            connection.disconnect();


            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder xmlbuilder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(responseString));
            Document xmlResponse = xmlbuilder.parse(is);


            if(xmlResponse.getElementsByTagName("Item").getLength() > 0){
                String title = ""; //OK
                String author = ""; //OK
                String datePublication = ""; //OK
                String publisher = ""; //OK
                String nbPages = ""; //OK
                String category = "";
                String description = ""; //OK


                Node itemNode =  xmlResponse.getElementsByTagName("Item").item(0);
                Book newBook = BookLibrary.getInstance().getNewBook();

                for(int i = 0; i < itemNode.getChildNodes().getLength(); i++){
                    Node item = itemNode.getChildNodes().item(i);
                    String test = item.getNodeName();
                    switch(item.getNodeName()){
                        case "ItemAttributes" :
                            for(int j = 0; j < item.getChildNodes().getLength(); j++) {
                                Node itemAttribute = item.getChildNodes().item(j);
                                switch (itemAttribute.getNodeName()) {
                                    case "Author":
                                        //Ici faire addAuthor plutot, vu que y'a plusieurs auteurs
                                        author = itemAttribute.getTextContent();
                                        break;
                                    case "Publisher":
                                        publisher = itemAttribute.getTextContent();
                                        break;
                                    case "NumberOfPages":
                                        nbPages = itemAttribute.getTextContent();
                                        break;
                                    case "PublicationDate":
                                        datePublication = itemAttribute.getTextContent();
                                        break;
                                    case "Title":
                                        title = itemAttribute.getTextContent();
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;
                        case "EditorialReviews":
                            Node itemEditorial = itemNode.getChildNodes().item(i);
                            description = itemEditorial.getChildNodes().item(0).getChildNodes().item(1).getTextContent();
                            description = Html.fromHtml(description).toString();
                            break;
                        case "BrowseNodes":
                            category = "";
                            break;
                        case "LargeImage":
                            String urlPicture = item.getChildNodes().item(0).getChildNodes().item(0).getTextContent();
                            InputStream imageStream = (InputStream) new URL(urlPicture).getContent();
                            byte[] image = ByteStreams.toByteArray(imageStream);
                            newBook.setImage(image);
                            break;
                    }
                }

                newBook.setIsbn(isbns[0]);
                newBook.setTitle(title);
                newBook.setAuthor(author);
                newBook.setCategory(category);
                newBook.setDatePublication(datePublication);
                newBook.setEditor(publisher);
                newBook.setNbPages(Integer.parseInt(nbPages));
                newBook.setDescription(description);

                return newBook;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void initAPI() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] secretyKeyBytes = awsSecretKey.getBytes(UTF8_CHARSET);
        secretKeySpec =
                new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);
        mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(secretKeySpec);
    }

    public String sign(Map<String, String> params) {
        params.put("AWSAccessKeyId", awsAccessKeyId);
        params.put("Timestamp", timestamp());

        SortedMap<String, String> sortedParamMap =
                new TreeMap<String, String>(params);
        String canonicalQS = canonicalize(sortedParamMap);
        String toSign =
                REQUEST_METHOD + "\n"
                        + endpoint + "\n"
                        + REQUEST_URI + "\n"
                        + canonicalQS;

        String hmac = hmac(toSign);
        String sig = percentEncodeRfc3986(hmac);
        String url = "http://" + endpoint + REQUEST_URI + "?" +
                canonicalQS + "&Signature=" + sig;

        return url;
    }

    private String hmac(String stringToSign) {
        String signature = null;
        byte[] data;
        byte[] rawHmac;
        try {
            data = stringToSign.getBytes(UTF8_CHARSET);
            rawHmac = mac.doFinal(data);
            Base64 encoder = new Base64();
            signature = new String(encoder.encode(rawHmac));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
        }
        return signature;
    }

    private String timestamp() {
        String timestamp = null;
        Calendar cal = Calendar.getInstance();
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
        timestamp = dfm.format(cal.getTime());
        return timestamp;
    }

    private String canonicalize(SortedMap<String, String> sortedParamMap)
    {
        if (sortedParamMap.isEmpty()) {
            return "";
        }

        StringBuffer buffer = new StringBuffer();
        Iterator<Map.Entry<String, String>> iter =
                sortedParamMap.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, String> kvpair = iter.next();
            buffer.append(percentEncodeRfc3986(kvpair.getKey()));
            buffer.append("=");
            buffer.append(percentEncodeRfc3986(kvpair.getValue()));
            if (iter.hasNext()) {
                buffer.append("&");
            }
        }
        String cannoical = buffer.toString();
        return cannoical;
    }

    private String percentEncodeRfc3986(String s) {
        String out;
        try {
            out = URLEncoder.encode(s, UTF8_CHARSET)
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            out = s;
        }
        return out;
    }















}
