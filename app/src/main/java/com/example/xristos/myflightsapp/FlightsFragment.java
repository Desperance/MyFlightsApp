package com.example.xristos.myflightsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.Time;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FlightsFragment extends Fragment {

    private ArrayAdapter<String> mFlightsListAdapter;

    public FlightsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.flightsfragment, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                        // automatically handle clicks on the Home/Up button, so long
                               // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();
                if (id == R.id.action_refresh) {
                    /*FetchFlightsTask flightsTask = new FetchFlightsTask();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String origin = prefs.getString(getString(R.string.pref_origin_key),
                                                getString(R.string.pref_origin_default));
                    String destination = prefs.getString(getString(R.string.pref_destination_key),
                            getString(R.string.pref_destination_default));
                    flightsTask.execute(origin,destination,"2016-12-26","2016-12-28","1","true","800","EUR","10");
                    return true;*/
                    updateFlights();

                    return true;
                    }
                return super.onOptionsItemSelected(item);
            }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);



                mFlightsListAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_flights,
                        R.id.list_item_flights_textview, new ArrayList<String>());

        ListView flightsListView = (ListView) rootView.findViewById(R.id.listview_flights);
        flightsListView.setAdapter(mFlightsListAdapter);

        flightsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String flights = mFlightsListAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, flights);
                startActivity(intent);
            }
        });


        return rootView;
    }

    private void updateFlights(){

        FetchFlightsTask flightsTask = new FetchFlightsTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String origin = prefs.getString(getString(R.string.pref_origin_key),
                getString(R.string.pref_origin_default));
        String destination = prefs.getString(getString(R.string.pref_destination_key),
                getString(R.string.pref_destination_default));
        String departureDay = prefs.getString(getString(R.string.pref_departureDay_key),
                getString(R.string.pref_departureDay_default));
        String returnDay = prefs.getString(getString(R.string.pref_returnDay_key),
                getString(R.string.pref_returnDay_default));

        String numberOfPersons = prefs.getString(getString(R.string.pref_numberOfPersons_key),
                getString(R.string.pref_numberOfPersons_default));
        String nonStop = prefs.getString(getString(R.string.pref_nonStop_key),
                getString(R.string.pref_nonStop_default));
        String maxPrice = prefs.getString(getString(R.string.pref_maxPrice_key),
                getString(R.string.pref_maxPrice_default));
        String currency = prefs.getString(getString(R.string.pref_currency_key),
                getString(R.string.pref_currency_default));
        String numOfResults = prefs.getString(getString(R.string.pref_numOfResults_key),
                getString(R.string.pref_numOfResults_default));

        flightsTask.execute(origin,destination,departureDay,returnDay,numberOfPersons,nonStop,maxPrice,currency,numOfResults);


    }

    public void onStart() {

        super.onStart();
        updateFlights();
    }


    public class FetchFlightsTask extends AsyncTask<String ,Void ,String[]>{

        private final String LOG_TAG = FetchFlightsTask.class.getSimpleName();


                /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */


                        /**
         +         * Prepare the weather high/lows for presentation.
         +         */


                        /**
         +         * Take the String representing the complete forecast in JSON Format and
         +         * pull out the data we need to construct the Strings needed for the wireframes.
         +         *
         +         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         +         * into an Object hierarchy for us.
         +         */
                        private String[] getFlightsDataFromJson(String flightsJsonStr, int numresults)
                        throws JSONException {

                                // These are the names of the JSON objects that need to be extracted.
                                        final String AMA_RESULTS = "results";
                        final String AMA_ITINERARIES = "itineraries";
                        final String AMA_OUTBOUND = "outbound";
                        final String AMA_INBOUND = "inbound";
                        final String AMA_FLIGHTS = "flights";
                        final String AMA_DEPARTS_AT = "departs_at";
                        final String AMA_ORIGIN = "origin";
                            final String AMA_AIRPORT = "airport";
                            final String AMA__MARKETING_AIRLINE = "marketing_airline";
                            final String AMA__BOOKING_INFO = "booking_info";
                            final String AMA__TRAVEL_CLASS = "travel_class";
                            final String AMA__SEATS_REMAINING = "seats_remaining";
                            final String AMA_CURRENCY = "currency";
                            final String AMA_FARE = "fare";
                            final String AMA_TOTAL_PRICE = "total_price";

                            // Max num of results our app can carry is 5
                            int numOfResults ;
                            String currency;

                            JSONObject FlightsJson = new JSONObject(flightsJsonStr);


                            currency = FlightsJson.getString(AMA_CURRENCY);
                            JSONArray flightsArray = FlightsJson.getJSONArray(AMA_RESULTS);






                                if (flightsArray.length() < numresults)
                                    numOfResults = flightsArray.length();
                                else
                                numOfResults = numresults;

                                String[] resultStrs = new String[numOfResults];
                                for(int i = 0; i < numOfResults; i++) {
                                // For now, using the format "Day, description, hi/low"
                                        String departsOut;
                                    String departsIn;
                                String airportOut;
                                String airportIn;
                                    String airlineOut;
                                    String airlineIn;
                                    String travelClassOut;
                                    String travelClassIn;
                                    String seats_remainingOut;
                                    String seats_remainingIn;
                                    String tolatPrice;

                                        // Get the JSON object representing the day
                                                JSONObject resultFlight = flightsArray.getJSONObject(i);

                                        // The date/time is returned as a long.  We need to convert that
                                                // into something human-readable, since most people won't read "1400356800" as
                                                        // "this saturday".


                                        // description is in a child array called "weather", which is 1 element long.
                                                JSONObject itinerariesObject = resultFlight.getJSONArray(AMA_ITINERARIES).getJSONObject(0);
                                                JSONObject outboundObject = itinerariesObject.getJSONObject(AMA_OUTBOUND);
                                                JSONObject flightsObject = outboundObject.getJSONArray(AMA_FLIGHTS).getJSONObject(0);
                                                 departsOut = flightsObject.getString(AMA_DEPARTS_AT);
                                                 airlineOut = flightsObject.getString(AMA__MARKETING_AIRLINE);


                                    JSONObject originObject = flightsObject.getJSONObject(AMA_ORIGIN);
                                    airportOut = originObject.getString(AMA_AIRPORT);

                                    JSONObject bookingObject = flightsObject.getJSONObject(AMA__BOOKING_INFO);
                                    travelClassOut = bookingObject.getString(AMA__TRAVEL_CLASS);
                                    seats_remainingOut = bookingObject.getString(AMA__SEATS_REMAINING);







                                    JSONObject inboundObject = itinerariesObject.getJSONObject(AMA_INBOUND);
                                    flightsObject = inboundObject.getJSONArray(AMA_FLIGHTS).getJSONObject(0);
                                    departsIn = flightsObject.getString(AMA_DEPARTS_AT);
                                    airlineIn = flightsObject.getString(AMA__MARKETING_AIRLINE);

                                    originObject = flightsObject.getJSONObject(AMA_ORIGIN);
                                    airportIn = originObject.getString(AMA_AIRPORT);


                                     bookingObject = flightsObject.getJSONObject(AMA__BOOKING_INFO);
                                    travelClassIn = bookingObject.getString(AMA__TRAVEL_CLASS);
                                    seats_remainingIn = bookingObject.getString(AMA__SEATS_REMAINING);

                                    JSONObject fareObject = resultFlight.getJSONObject(AMA_FARE);
                                    tolatPrice = fareObject.getString(AMA_TOTAL_PRICE);




                                    // Temperatures are in a child object called "temp".  Try not to name variables
                                                // "temp" when working with temperature.  It confuses everybody.
                                                       /* JSONObject temperatureObject = resultFlight.getJSONObject(OWM_TEMPERATURE);
                                double high = temperatureObject.getDouble(OWM_MAX);
                                double low = temperatureObject.getDouble(OWM_MIN);*/

                                       /* highAndLow = formatHighLows(high, low);*/
                                resultStrs[i] = "ORIGIN:"+"\nOutbound: "+departsOut + "\nOutAirport: "+ airportOut +
                                       "\nAirline: "+ airlineOut + "\nTravel Class: " + travelClassOut +
                                        "\nRemaining seats: " + seats_remainingOut +
                                        "\n\nDESTINATION:" +
                                        "\nInbound: "+ departsIn +"\nInAirport: "+ airportIn +
                                        "\nAirline: "+ airlineIn + "\nTravel Class: " + travelClassIn +
                                        "\nRemaining seats: " + seats_remainingIn +
                                "\n\nTotal price: " + tolatPrice + currency;
                            }

                                for (String s : resultStrs) {
                                Log.v(LOG_TAG, "Forecast entry: " + s);
                            }
                        return resultStrs;

                            }






        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                                return null;
                            }

            int maxResults;

            // These two need to be declared outside the try/catch
                        // so that they can be closed in the finally block.
                                HttpURLConnection urlConnection = null;
                                BufferedReader reader = null;

                                // Will contain the raw JSON response as a string.
                                String flightsJsonStr = null;

                                //String format = "json";
                                //String units = "metric";
                                //int numDays = 7;
                                String myApiKey = "xszkIpGFqbZwxGN7U7VHhf5qLadaCwtz";

                                try {
                                    maxResults = Integer.parseInt(params[8]) ;
                                // Construct the URL for the OpenWeatherMap query
                                        // Possible parameters are avaiable at OWM's forecast API page, at
                                                // http://openweathermap.org/API#forecast
                                    final String FLIGHTS_BASE_URL =
                                                                    "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?";
                                                    final String APIKEY = "apikey";
                                                   // final String FORMAT_PARAM = "mode";
                                                    final String ORIGIN = "origin";
                                                    final String DESTINATION = "destination";
                                                    final String DEPARTURE_DATE = "departure_date";
                                                    final String REUTRN_DATE = "return_date";
                                                    final String ADULTS = "adults";
                                                    final String NONSTOP = "nonstop";
                                                    final String MAX_PRICE = "max_price";
                                                    final String CURRENCY = "currency";
                                                    final String NUMBER_OF_RESULTS = "number_of_results";


                                                            Uri builtUri = Uri.parse(FLIGHTS_BASE_URL).buildUpon()
                                                                    .appendQueryParameter(APIKEY, myApiKey)
                                                                    .appendQueryParameter(ORIGIN, params[0])
                                                                    .appendQueryParameter(DESTINATION, params[1])
                                                                    .appendQueryParameter(DEPARTURE_DATE, params[2])
                                                                    .appendQueryParameter(REUTRN_DATE, params[3])
                                                                    .appendQueryParameter(ADULTS, params[4])
                                                                    .appendQueryParameter(NONSTOP, params[5])
                                                                    .appendQueryParameter(MAX_PRICE, params[6])
                                                                    .appendQueryParameter(CURRENCY, params[7])
                                                                    .appendQueryParameter(NUMBER_OF_RESULTS, params[8])

                                                                    .build();

                                                            URL url = new URL(builtUri.toString());

                                                            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                                        // Create the request to OpenWeatherMap, and open the connection
                                                urlConnection = (HttpURLConnection) url.openConnection();
                                urlConnection.setRequestMethod("GET");
                                urlConnection.connect();

                                        // Read the input stream into a String
                                                InputStream inputStream = urlConnection.getInputStream();
                                StringBuffer buffer = new StringBuffer();
                                if (inputStream == null) {
                                        // Nothing to do.
                                                return null;
                                    }
                                reader = new BufferedReader(new InputStreamReader(inputStream));

                                        String line;
                                while ((line = reader.readLine()) != null) {
                                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                                                // But it does make debugging a *lot* easier if you print out the completed
                                                        // buffer for debugging.
                                                                buffer.append(line + "\n");
                                    }

                                        if (buffer.length() == 0) {
                                        // Stream was empty.  No point in parsing.
                                                return null;
                                    }
                                flightsJsonStr = buffer.toString();

                                Log.v(LOG_TAG, "Forecast JSON String" +flightsJsonStr);

                            } catch (IOException e) {
                                Log.e(LOG_TAG, "Error ", e);
                                    return null;
                                // If the code didn't successfully get the weather data, there's no point in attemping
                                        // to parse it.+                return null;

                                } finally {
                                if (urlConnection != null) {
                                        urlConnection.disconnect();
                                    }
                                if (reader != null) {
                                        try {
                                                reader.close();
                                            } catch (final IOException e) {
                                                Log.e(LOG_TAG, "Error closing stream", e);
                                            }
                                    }
                            }

                            try {
                                return getFlightsDataFromJson(flightsJsonStr, maxResults);
                            } catch (JSONException e) {
                                Log.e(LOG_TAG, e.getMessage(), e);
                                e.printStackTrace();
                            }

                        return null;

        }

                        protected void onPostExecute(String[] result) {
                                if (result != null) {
                                mFlightsListAdapter.clear();
                                for(String flightsStr : result) {
                                        mFlightsListAdapter.add(flightsStr);
                                    }
                                // New data is back from the server.  Hooray!
                                    }
                    }

    }

}
