/*
 * Coin Collection, an Android app that helps users track the coins that they've collected
 * Copyright (C) 2010-2016 Andrew Williams
 *
 * This file is part of Coin Collection.
 *
 * Coin Collection is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Coin Collection is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Coin Collection.  If not, see <http://www.gnu.org/licenses/>.
 */

/***
 This file pulls in the AsyncTask code from cw-android
 Copyright (c) 2008-2012 CommonsWare, LLC
 Modified by Andrew Williams
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 http://commonsware.com/Android

 https://raw.github.com/commonsguy/cw-android/master/Rotation/RotationAsync/src/com/commonsware/android/rotation/async/RotationAsync.java

 */

package com.spencerpages;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * The main Activity for the app.  Implements a ListView which lets the user view a previously
 * created collection or add/delete/reorder/export/import collections
 */
public class MainActivity extends AppCompatActivity {
    
	private final ArrayList<CollectionListInfo> mCollectionListEntries = new ArrayList<>();
	private final Context mContext = this;
	private FrontAdapter mListAdapter;
    private DatabaseAdapter mDbAdapter;

    // As a hack to get the static strings at the bottom of the list, we add spacers into
    // mCollectionListEntries.  This tracks the number of those spacers, which we use in several
    // places.
    //  - Add Collection
    //  - Remove Collection
    //  - Import Collections
    //  - Export Collections
    //  - Re-order Collections
    //  - Info
    private final static int NUMBER_OF_COLLECTION_LIST_SPACERS = 6;

    // The number of actual collections in mCollectionListEntries
    private int mNumberOfCollections;
	
	// To be used with a simple cancel-able alert.  For more complicated alerts use a different one
	private AlertDialog.Builder mBuilder = null;

    // TODO Document
	private String[] mTypesOfCoins;
    private String[] mReverseCoinImages;
    private String[] mObverseCoinImages;

	// Used for the Update Database functionality
    private ProgressDialog mProgressDialog = null;
	private InitTask mTask = null;
	
	private boolean mDatabaseHasBeenOpened = false;
	private boolean mIsImportingCollection = false;
	
	// See notes in onCreate below.  Used to handle the case where we are importing collections and
    // the screen orientation changes
    private boolean mShouldFinishViewSetupToo = false;
	
	// These are used to support importing the collection data.  After we have read everything in,
    // we save the info here while we ask the user whether they really want to delete all of their
    // existing collections.
    // TODO Rename this to indicated that they are associated with importing
    private ArrayList<String[]> mCollectionInfo = null;
	private ArrayList<String[][]> mCollectionContents = null;
    private int mDatabaseVersion = -1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.main_activity_layout);

		if(BuildConfig.DEBUG) {
    		
    		// Run unit test
    		UnitTests test = new UnitTests();
    		boolean result = test.runTests(this);
    		
    		if(!result){
    			// return, so that we will see something went wrong
    			Log.e(MainApplication.APP_NAME, "Failed Unit Tests");
    			finish();
    			return;
    		} else {
    			Log.e(MainApplication.APP_NAME, "Unit Tests finished successfully");
    		}
    		
    	}
    	
    	mBuilder = new AlertDialog.Builder(this);
    	
    	// Check whether it is the users first time using the app
        final SharedPreferences mainPreferences = getSharedPreferences(MainApplication.PREFS, MODE_PRIVATE);

    	// In legacy code we used first_Time_screen2 here so that the message would be displayed
        // until they made it to the create collection screen.  That isn't necessary anymore, but
        // if they are upgrading from that don't show them the help screen if first_Time_screen1
        // isn't set
    	if(mainPreferences.getBoolean("first_Time_screen1", true) && mainPreferences.getBoolean("first_Time_screen2", true)){
    		// Show the user how to do everything
    		String text = "Thanks for downloading Coin Collection!\nTo get started, select 'New Collection'";
    		
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(text)
    		       .setCancelable(false)
    		       .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		               dialog.cancel();
    		       		   SharedPreferences.Editor editor = mainPreferences.edit();
    		       		   editor.putBoolean("first_Time_screen1", false);
    		       		   editor.commit(); // .apply() in later APIs
    		           }
    		       });
    		AlertDialog alert = builder.create();
    		alert.show();
    	}
    	
    	InitTask check = (InitTask) getLastCustomNonConfigurationInstance();
    	
		// TODO If there is a screen orientation change, it looks like a ProgressDialog gets leaked. :(
		if(check == null){

            if(BuildConfig.DEBUG) {
                Log.d(MainApplication.APP_NAME, "No previous state so kicking off InitTask to doOpen");
            }

            // Kick off the InitTask to open the database.  This will likely be the first open,
	    	// so we want it in the AsyncTask in case we have to go into onUpgrade and it takes
	    	// a long time.

		    mTask = new InitTask();

		    mTask.doOpen = true;
            mTask.activity = this;

		    mTask.execute();
		    
		    // The InitTask will call finishViewSetup once the database has been opened
		    // for the first time

		} else {

            if(BuildConfig.DEBUG) {
                Log.d(MainApplication.APP_NAME, "Taking over existing mTask");
            }
						
			// an InitTask is running, make a new dialog to show it
			mTask = check;
            mTask.activity = this;

			// There's two possible InitTask's that could be running:
			//     - The one to open the database for the first time
			//     - The one to import collections
			// In the case of the former, we just want to show the dialog that the user had on the
            // screen.  For the latter case, we still need something to call finishViewSetup, and
            // we don't want to call it here bc it will try to use the database too early.  Instead,
            // set a flag that will have that InitTask call finishViewSetup for us as well.
			
			if(mProgressDialog != null && mProgressDialog.isShowing()){
				mProgressDialog.dismiss();
			}
			
			String message = mTask.openMessage;
			
			if(mTask.doImport){
				message = mTask.importMessage;

				this.mDatabaseHasBeenOpened = true; // This has to have happened at this point
				this.mIsImportingCollection = true;
				this.mShouldFinishViewSetupToo = true;
			}
			// Make a new dialog
			
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setMessage(message);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setProgress(0);
			mProgressDialog.show();
			
		}

        // HISTORIC - no longer the case:
        //
    	// We need to open the database since we are the first activity to run
    	// We only want to open the database once, though, because it is a hassle
    	// in that it is very expensive and must be opened in an asynchronous mTask
    	// background thread as to avoid getting application not responding errors.
    	// So, here is what we do:
    	// - Instantiate a Service that will hold the database adapter
    	//    + This will be around for the lifetime of the application
    	// - Once the service has been created, open up the database in an async mTask
    	// - Once the database is open, finish setting up the UI from the data pulled
    	
    	// After we open it, it must have at least one activity bound to it at all times
    	// for it to stay alive.  So, each activity must bind to it on onCreate and 
    	// unbind in onDestroy.  Once the app is terminating, all the activity onDestroy's
    	// will have been called and the service's onDestroy will then get called, where 
    	// we close the databse
    	
    	// Actually instantiate the database service
        //Intent mServiceIntent = new Intent(this, DatabaseService.class);
        // and bind to it
        //bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        //
        // Having the database open for a long time didn't seem to work out well, though - after
        // having the app open for a while, database errors would start popping up.  Now, we just
        // do the first DB open in an AsyncTask to ensure we don't get an ANR if a database upgrade
        // is required, but just open and close the database regularly as needed after that.
    }

    /**
     * Finishes setting up the view once the database has been opened.
     */
    private void finishViewSetup(){

        // Called from the InitTask after the database has been successfully
        // opened for the first time.  Now we can be fairly sure that future
        // open's won't trigger an ANR issue.

		if(BuildConfig.DEBUG) {
            Log.v("mainActivity", "finishViewSetup");
        }

        mTypesOfCoins = getResources().getStringArray(R.array.types_of_coins);
        mReverseCoinImages = getResources().getStringArray(R.array.reverse_of_coins);
        mObverseCoinImages = getResources().getStringArray(R.array.obverse_of_coins);

  	    // Instantiate the DatabaseAdapter
		mDbAdapter = new DatabaseAdapter(this);

        // Populate mCollectionListEntries with the data from the database
        updateCollectionListFromDatabase();

        // Instantiate the FrontAdapter
        mListAdapter = new FrontAdapter(mContext, mCollectionListEntries, mNumberOfCollections);

    	ListView lv = (ListView) findViewById(R.id.main_activity_listview);

    	lv.setAdapter(mListAdapter);

        // TODO Not sure what this does?
        lv.setTextFilterEnabled(true); // Typing narrows down the list

        // For when we use fragments, listen to the backstack so we can transition back here from
        // the fragment

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {

                        if(0 == getSupportFragmentManager().getBackStackEntryCount()){

                            // We are back at this activity, so restore the ActionBar
                            getSupportActionBar().setTitle("Coin Collection");
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            getSupportActionBar().setHomeButtonEnabled(false);

                            // The collections may have been re-ordered, so update them here.
                            updateCollectionListFromDatabase();

                            // Change it out with the new list
                            mListAdapter.items = mCollectionListEntries;
                            mListAdapter.numberOfCollections = mNumberOfCollections;
                            mListAdapter.notifyDataSetChanged();
                        }
                    }
                });

        // Now set the onItemClickListener to perform a certain action based on what's clicked
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		
        		// See whether it was one of the special list entries (Add collection, delete
                // collection, etc.)
                if(position >= mNumberOfCollections){
                	
                	int newPosition = position - mNumberOfCollections;
                    Intent intent;
                	
                	switch(newPosition){
                	    case 0: // Add Collection
                    		intent = new Intent(mContext, CoinPageCreator.class);
                    		startActivity(intent);
                	    	break;
                	    case 1: // Remove Collection
                	    	
                            if(mNumberOfCollections == 0){
                        		Toast.makeText(mContext, "No Collections Found", Toast.LENGTH_SHORT).show();
                        		break;
                            }
                        	// Thanks!
                        	// http://stackoverflow.com/questions/2397106/listview-in-alertdialog
                            CharSequence[] names = new CharSequence[mNumberOfCollections];
                        	for(int i = 0; i < mNumberOfCollections; i++){
                        		names[i] = mCollectionListEntries.get(i).getName();
                        	}
                        	
                        	AlertDialog.Builder delete_builder = new AlertDialog.Builder(mContext);
                        	delete_builder.setTitle("Select a collection to delete");
                        	delete_builder.setItems(names, new DialogInterface.OnClickListener() {
                        	    public void onClick(DialogInterface dialog, int item) {
                        	    	showDeleteConfirmation(mCollectionListEntries.get(item).getName());
                        	    }
                        	});
                        	AlertDialog alert = delete_builder.create();
                        	alert.show();
                	    	break;
                	    case 2: // Import Collections
                	    	handleImportCollectionsPart1();
                	    	break;
                	    case 3: // Export Collections
                	    	handleExportCollectionsPart1();
                	    	break;
                        case 4: // Re-order Collections

                            if(mNumberOfCollections == 0){
                                Toast.makeText(mContext, "No Collections Found", Toast.LENGTH_SHORT).show();
                                break;
                            }

                            // Get a list that excludes the spacers
							List<CollectionListInfo> tmp = mCollectionListEntries.subList(0, mNumberOfCollections);
							ArrayList<CollectionListInfo> collections = new ArrayList<>(tmp);

                            ReorderCollections fragment = new ReorderCollections();
                            fragment.setCollectionList(collections);

                            // Show the fragment used for reordering collections
                            getSupportFragmentManager().beginTransaction()
                                    .add(R.id.main_activity_frame, fragment, "ReorderFragment")
                                    .addToBackStack(null)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit();

							// Setup the actionbar for the reorder page
							// TODO Check for NULL
							getSupportActionBar().setTitle("Reorder Collections");
							getSupportActionBar().setDisplayHomeAsUpEnabled(true);
							getSupportActionBar().setHomeButtonEnabled(true);

							break;
                	    case 5: // About
                        	//Context mContext = getApplicationContext();
                        	LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                        	View layout = inflater.inflate(R.layout.info_popup,
                        	                               (ViewGroup) findViewById(R.id.layout_root));

                        	AlertDialog.Builder info_builder = new AlertDialog.Builder(mContext);
                        	info_builder.setView(layout);
                        	AlertDialog alertDialog = info_builder.create();
                        	alertDialog.show();
                	    	break;
                	}
                	
                	return;
                }
                // If it gets here, the user has selected a collection
            	
        		Intent intent = new Intent(mContext, CollectionPage.class);

                CollectionListInfo listEntry = mCollectionListEntries.get(position);

        		intent.putExtra("Collection_Name", listEntry.getName());
        		intent.putExtra("Coin_Type", listEntry.getType());
        		intent.putExtra("Image_Identifier", listEntry.getCoinObverseImageIdentifier());
        		intent.putExtra("Coin_Type_Image", listEntry.getCoinReverseImageIdentifier());

        		startActivity(intent);
        	}
        });
    }

    /**
     * Kicks off the import process by reading in the import files into our internal representation.
     * Once this is complete, it kicks off an AsyncTask to actually store the data in the database.
     */
    private void handleImportCollectionsPart1(){
    	   	
    	// See whether we can read from the external storage
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// Should be able to read from it without issue
		} else if (Environment.MEDIA_SHARED.equals(state)) {
			// Shared with PC so can't write to it
    		showCancelableAlert("External media cannot be read from.  If plugged in to a computer, try unplugging it.");
        	return;		
		} else {
			// Doesn't exist, so notify user
			showCancelableAlert("External media cannot be read from (state: " + state + ".)  Try again later.");
        	return;
		}
		
    	//http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder
    	File sdCard = Environment.getExternalStorageDirectory();
    	String exportFolderName = "/coin-collection-app-files";
    	String path = sdCard.getAbsolutePath() + exportFolderName;
    	File dir = new File(path);

    	if(!dir.isDirectory()){
    		// The directory doesn't exist, notify the user
    		showCancelableAlert("Failed to find existing export directory at " + path);
        	return;
    	}
    	
    	File file;
    	FileInputStream f;
    	InputStreamReader in_;
    	BufferedReader in;
    	boolean errorOccurred = false;

        // Read the database version
        file = new File(dir, "database_version.txt");

        try {
            f = new FileInputStream(file);
            in_ = new InputStreamReader(f, "UTF8");
            in = new BufferedReader(in_);
            this.mDatabaseVersion = Integer.parseInt(in.readLine());

        } catch (Exception e) {
            showCancelableAlert("Error when opening input file for reading at " + file.getAbsolutePath());
            return;
        }

        if(-1 == this.mDatabaseVersion){
            showCancelableAlert("Invalid database version");
            return;
        }

        try {
            in.close();
        } catch (IOException e) {
            showCancelableAlert("Error when closing input file at " + file.getAbsolutePath());
            return;
        }

        // Read the collection_info table
    	file = new File(dir, "list-of-collections.csv");

    	try {
			f = new FileInputStream(file);
			in_ = new InputStreamReader(f, "UTF8");
			in = new BufferedReader(in_);
		} catch (Exception e) {
			showCancelableAlert("Error when opening input file for reading at " + file.getAbsolutePath());
			return;
		}
    	
		ArrayList<String[]> collectionInfo = new ArrayList<>();

    	try {
        	String line = in.readLine();
    	    while(null != line){
    	    	
    	    	// Strip out all bad characters.  They shouldn't be there anyway ;)
    	    	line = line.replace('[', ' ');
    	    	line = line.replace(']', ' ');

        		// name, coinType, total, max. display
    	    	String[] items = line.split(",");
    	    	
    	    	// Perform some sanity checks here
                int numberOfColumns = 5;

    	    	if(items.length != numberOfColumns){
    	    		errorOccurred = true;
    	    		showCancelableAlert("Invalid backup file detected (Error 1)");
    	    		break;
    	    	}
    	    	
    	    	// Must be a valid coin type
    	    	for(int i = 0; i < mTypesOfCoins.length; i++){
    	    		
    	    		if(mTypesOfCoins[i].equals(items[1])){
    	    			break;
    	    		} else if(mTypesOfCoins.length == i + 1){
    	    			errorOccurred = true;
        	    		showCancelableAlert("Invalid backup file detected (Error 2)");
    	    			break;
    	    		}
    	    		
    	    	}
    	    	
    	    	if(errorOccurred){
    	    		break;
    	    	}
    	    	
    	    	// Must have a positive number collected and a positive
    	    	// max
    	    	if(Integer.valueOf(items[2]) < 0 ||
    	    	   Integer.valueOf(items[3]) < 0){
    	    		errorOccurred = true;
    	    		showCancelableAlert("Invalid backup file detected (Error 3)");
	    			break;
    	    	}
    	    	
    	    	// Must not have a name that is the same as a previous one
    	    	for(int i = 0; i < collectionInfo.size(); i++){
    	    		
    	    		String[] previousCollectionInfo = collectionInfo.get(i);
    	    		if(items[0].equals(previousCollectionInfo[0])){
    	    			errorOccurred = true;
        	    		showCancelableAlert("Invalid backup file detected (Error 4)");
    	    			break;
    	    		}
    	    	}
    	    	
    	    	if(errorOccurred){
    	    		break;
    	    	}
    	    	
    	    	// Good to go, add it to the list
                collectionInfo.add(items);
    	    	
    	    	line = in.readLine();
    	    }
    	} catch(Exception e){
    		errorOccurred = true;
    		showCancelableAlert("An unknown error occurred when trying to read in collection at " + file.getAbsolutePath());
    	}
    	
    	try {
			in.close();
		} catch (IOException e) {
			showCancelableAlert("Error when closing input file at " + file.getAbsolutePath());
			return;
		}
    	
    	if(errorOccurred){
    		// Don't continue on
    		return;
    	}
    	
    	// The ArrayList will be indexed by collection, the outer String[] will be indexed
    	// by line number, and the inner String[] will be each cell in the row
    	ArrayList<String[][]> collectionContents = new ArrayList<>();
    	 
    	// We loaded in the collection "metadata" table, so now load in each collection
    	for(int i = 0; i < collectionInfo.size(); i++){

    		String[] collectionData = collectionInfo.get(i);

            // Undo the '/' to '_SL_' translation that we did when exporting to avoid having '/'
            // characters in the export file names (which the OS interprets as directory delimiters)
            String collectionFileName = collectionData[0].replaceAll("/", "_SL_");

    		file = new File(dir, collectionFileName + ".csv");

    		if(!file.isFile()){
    			showCancelableAlert("Couldn't find collection input file at " + file.getAbsolutePath());	
    			return;
    		}

    		try {
    			f = new FileInputStream(file);
    			in_ = new InputStreamReader(f, "UTF8");
    			in = new BufferedReader(in_);
    		} catch (Exception e) {
    			showCancelableAlert("Error when opening input file for reading at " + file.getAbsolutePath());	
    			return;
    		}

    		ArrayList<String[]> collectionContent = new ArrayList<>();

    		try {
    			String line = in.readLine();
    			while(null != line){

    				// Strip out all bad characters.  They shouldn't be there anyway ;)
    				line = line.replace('[', ' ');
    				line = line.replace(']', ' ');

    				// coinIdentifier, coinMint, inCollection, advGradeIndex, advQuantityIndex, advNotes
    				String[] items = line.split(",", -1); // -1 to not skip empty entries

    				// Perform some sanity checks here
        	    	int numberOfColumns = 6;

    				if(items.length != numberOfColumns){
    					errorOccurred = true;
    					showCancelableAlert("Invalid backup file detected (Error 11) " + String.valueOf(items.length));
    					break;
    				}

    				// TODO Maybe add more checks
    				collectionContent.add(items);

    				line = in.readLine();
    			}  

    		} catch(Exception e){
    			errorOccurred = true;
    			showCancelableAlert("An unknown error occurred when trying to read in collection at " + file.getAbsolutePath());
    		}
    		
        	if(errorOccurred){
        		// Don't continue on
        		try {
        			in.close();
        		} catch (IOException e) {
        			return;
        		}
        		return;
        	}
    		
    		// Verify that we read in the correct number of records
    		if(collectionContent.size() != Integer.valueOf(collectionData[3])){
    			errorOccurred = true;
    			showCancelableAlert("Invalid backup file detected (Error 12)");
    		}
    		
    		// TODO Can this happen? ClassCastException Object[] cannot be cast to String[][]
    		collectionContents.add(collectionContent.toArray(new String[0][]));

    		try {
    			in.close();
    		} catch (IOException e) {
    			showCancelableAlert("Error when closing input file at " + file.getAbsolutePath());
    			return;
    		}
        	
        	if(errorOccurred){
        		// Don't continue on
        		return;
        	}
    	}
    	
    	// Cool, at this point we've read in the data successfully and we've passed all of
    	// the sanity checks.  We should put this data aside, show the user a message to
    	// have them confirm that they want to do this... Although if they don't have any
    	// collections we can optimize this step out
    	this.mCollectionInfo = collectionInfo;
    	this.mCollectionContents = collectionContents;

    	if(0 == mNumberOfCollections){
            // Finish the import by kicking off an AsyncTask to do the heavy lifting    		
		    mTask = new InitTask();

		    mTask.doImport = true;
            mTask.activity = this;

		    mTask.execute();
		    
    	} else {
    	    showImportConfirmation();
    	}
    }
    
    private void handleImportCollectionsCancel(){
    	// Release the memory associated with the collection info we read in
    	this.mCollectionInfo = null;
    	this.mCollectionContents = null;
        this.mDatabaseVersion = -1;
    }

    /**
     * Finishes strong with some heavy lifting (putting the imported data into the database.)  This
     * should be done from an AsyncTask, since it could cause an ANR error if done on the main
     * thread!
     */
    private void handleImportCollectionsPart2(){
    	
    	// Take the data we've stored and replace what's in the database with it
    	
    	// NOTE We can't use the showCancelableAlert here because this doesn't get
    	// executed on the main thread.
    	
		mDbAdapter.open();
    	
    	// TODO Consider how to make this more robust to failures
    	for(int i = 0; i < mNumberOfCollections; i++){
    		
    		CollectionListInfo info = mCollectionListEntries.get(i);
    		mDbAdapter.dropTable(info.getName());
    	}
    	
    	mDbAdapter.dropCollectionInfoTable();
    	
    	mDbAdapter.createCollectionInfoTable();

    	for(int i = 0; i < mCollectionInfo.size(); i++){
    		String[] collectionInfo = mCollectionInfo.get(i);
    		String[][] collectionContents = mCollectionContents.get(i);
    		
    		String name = collectionInfo[0];
    		String coinType = collectionInfo[1];
    		int total = Integer.valueOf(collectionInfo[3]);
    		int advView = Integer.valueOf(collectionInfo[4]);

    		mDbAdapter.createNewTable(name, coinType, total, advView, i, collectionContents);
    	}
    	
    	// Release the memory associated with the collection info we read in
    	mCollectionInfo = null;
    	mCollectionContents = null;

        // Update any imported tables, if necessary
        if(mDatabaseVersion != mDbAdapter.DATABASE_VERSION) {
            mDbAdapter.upgradeCollections(mDatabaseVersion);
        }
        mDatabaseVersion = -1;
    	
    	mDbAdapter.close();
    	
    	// Looks like the view gets reloaded automatically... Hooray!
    }

    /**
     * Begins the collection export process by doing some preliminary external media checks and
     * prompts the user if an export will overwrite previous backup files.
     */
    private void handleExportCollectionsPart1(){
    	// TODO Move this function to be more resistant to ANR, if reports show that it is a
        // problem
    	
    	// See whether we can write to the external storage
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// Should be able to write to it without issue
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// Can't write to it, so notify user
			showCancelableAlert("External media cannot be written to (mounted read-only.)  If plugged in to a computer, try unplugging it.");
        	return;
		} else if (Environment.MEDIA_SHARED.equals(state)) {
			// Shared with PC so can't write to it
			showCancelableAlert("External media cannot be written to.  If plugged in to a computer, try unplugging it.");
        	return;			
		} else {
			// Doesn't exist, so notify user
			showCancelableAlert("External media cannot be written to (state: " + state + ".)  Try again later.");
        	return;
		}
    	
    	//http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder
    	File sdCard = Environment.getExternalStorageDirectory();
    	String exportFolderName = "/coin-collection-app-files";
    	String path = sdCard.getAbsolutePath() + exportFolderName;
    	File dir = new File(path);

    	if(dir.isDirectory() || dir.exists()){
    		// Let the user decide whether they want to delete this
    		showExportConfirmation();
    	} else {
    		// Proceed with exporting directly
    		handleExportCollectionsPart2();
    	}
    }

    /**
     * Finishes the collection export process (after giving the user a chance to cancel if this will
     * cause an existing backup to be deleted.)
     */
    private void handleExportCollectionsPart2(){
    	
    	// At this point we know we can write to storage and the user is ok
    	// if we blow away existing imported files
    	
    	// TODO Move this function to be more resistant to ANR, if necessary
    	
    	//http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder
    	File sdCard = Environment.getExternalStorageDirectory();
    	String exportFolderName = "/coin-collection-app-files";
    	String path = sdCard.getAbsolutePath() + exportFolderName;
    	File dir = new File(path);

    	if(!dir.isDirectory() && !dir.mkdir()){
    		// The directory doesn't exist, notify the user
    		showCancelableAlert("Failed to make/find directory at " + path);
        	return;
    	}
    	
    	File file;
    	FileOutputStream f;
    	OutputStreamWriter out;
    	boolean errorOccurred = false;

    	// Write out the collection_info table
    	file = new File(dir, "list-of-collections.csv");

    	try {
			f = new FileOutputStream(file);
			out = new OutputStreamWriter(f, "UTF8");
		} catch (Exception e) {
			// This shouldn't happen since we just created it
			showCancelableAlert("Error when opening output file for writing at " + file.getAbsolutePath());
			return;
		}
    	
		mDbAdapter.open();

    	// Iterate through the list of collections and write the files
    	for(int i = 0; i < mNumberOfCollections; i++){
    		// name, coinType, total, max, display
    		CollectionListInfo item = mCollectionListEntries.get(i);
    		String name = item.getName();
    		String type = item.getType();
    		int totalCollected = item.getCollected();
    		int total = item.getMax();
    		    		
    		// Strip comma's from the name
    		String cleanName = name.replace(',', ' ');
    		
    		try {
	    		out.append(cleanName + "," +
	    		          type + "," +
	    		          String.valueOf(totalCollected) + "," +
	    		          String.valueOf(total));
	    		
				int display = mDbAdapter.fetchTableDisplay(name);
				out.append("," + String.valueOf(display));

	    		if(mNumberOfCollections != i + 1){
	    		    out.append("\n");
	    		}
    		} catch(Exception e) {
    			showCancelableAlert("Error when writing to output file at " + file.getAbsolutePath());
                errorOccurred = true;
    			break;
    		}
    	}
    	
    	try {
			out.close();
		} catch (IOException e) {
			showCancelableAlert("Error when closing output file at " + file.getAbsolutePath());
			return;
		}
    	
    	if(errorOccurred){
    		return;
    	}
    	
    	// Write out the database version
    	file = new File(dir, "database_version.txt");

    	try {
			f = new FileOutputStream(file);
			out = new OutputStreamWriter(f, "UTF8");
			
			out.write(String.valueOf(DatabaseAdapter.DATABASE_VERSION));
			
			out.close();
		} catch (Exception e) {
			// This shouldn't happen since we just created it
			showCancelableAlert("Error when working with output file at " + file.getAbsolutePath());
			return;
		}
    	
    	// Write out all of the other tables
    	for(int i = 0; i < mNumberOfCollections; i++){
    		CollectionListInfo item = mCollectionListEntries.get(i);
    		String name = item.getName();
    		
    		// Strip comma's from the name
    		String cleanName = name.replace(',', ' ');

            // Handle '/''s in the file names (otherwise importing will fail, because the OS will
            // think the '/' characters are folder delimiters.)  This will be undone when we import.
            cleanName = cleanName.replaceAll("/", "_SL_");

            file = new File(dir, cleanName + ".csv");

        	try {
    			f = new FileOutputStream(file);
    			out = new OutputStreamWriter(f, "UTF8");
    		} catch (Exception e) {
    			// This shouldn't happen since we just created it
    			showCancelableAlert("Error when opening output file for writing at " + file.getAbsolutePath());
    			return;
    		}
        	
        	// coinIdentifier, coinMint, inCollection, advGradeIndex, advQuantityIndex, advNotes
        	Cursor resultCursor = mDbAdapter.getAllCollectionInfo(name);
        	boolean isFirstLine = true;
        	if (resultCursor.moveToFirst()) {
        		do {
        			ArrayList<String> info = new ArrayList<>();
        			info.add(resultCursor.getString(resultCursor.getColumnIndex("coinIdentifier")));
        			info.add(resultCursor.getString(resultCursor.getColumnIndex("coinMint")));
        			info.add(String.valueOf(resultCursor.getInt(resultCursor.getColumnIndex("inCollection"))));
					info.add(String.valueOf(resultCursor.getInt(resultCursor.getColumnIndex("advGradeIndex"))));
        			info.add(String.valueOf(resultCursor.getInt(resultCursor.getColumnIndex("advQuantityIndex"))));
        			info.add(resultCursor.getString(resultCursor.getColumnIndex("advNotes")));

        			if(isFirstLine){
        				isFirstLine = false;
        			} else {
        				// Prepend the newline.  We do it here as an easy way to know that there won't
        				// be a blank line at the end of this file
        				try {
        				    out.write("\n");
        				} catch(Exception e) {
        					showCancelableAlert("Error when writing to output file at " + file.getAbsolutePath());
        					errorOccurred = true;
        					break;
        				}  
        			}

        			for(int k = 0; k < info.size(); k++){
        				String infoItem = info.get(k);

        				try {
        					out.write(infoItem);
        					if(info.size() != k + 1){
								// Add the comma after all but the last column
        						out.write(",");
        					}

        				} catch(Exception e) {
        					showCancelableAlert("Error when writing to output file at " + file.getAbsolutePath());
        					errorOccurred = true;
        					break;
        				}
        			}

        			if(errorOccurred){
        				break;
        			}

        		}while(resultCursor.moveToNext());
        	}
        	resultCursor.close();
        	
        	try {
    			out.close();
    		} catch (IOException e) {
    			showCancelableAlert("Error when closing output file at " + file.getAbsolutePath());
    			return;
    		}
        	
        	if(errorOccurred){
        		return;
        	}
    	}
    	
    	mDbAdapter.close();
    	
		showCancelableAlert("Successfully exported collection to '" + exportFolderName + "' on the external storage device!");
    }
    
    private void showCancelableAlert(String text) {
		mBuilder.setMessage(text).setCancelable(true);
		AlertDialog alert = mBuilder.create();
		alert.show();
	}

	// Need to make our own Array Adapter to handle the special list (list of collections + entries
    // for 'Create Collections', 'Reorder Collections', etc.)
    // Thanks! http://www.softwarepassion.com/android-series-custom-listview-items-and-adapters/
    private class FrontAdapter extends ArrayAdapter<CollectionListInfo> {

        public ArrayList<CollectionListInfo> items;
        public int numberOfCollections;

        public FrontAdapter(Context context, ArrayList<CollectionListInfo> items, int numberOfCollections) {
            super(context, R.id.textView1, items);
            this.items = items;
            this.numberOfCollections = numberOfCollections;
        }
        
        @Override
        public int getViewTypeCount(){
        	return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if(position >= this.numberOfCollections){
            	return 1;
            } else {
            	return 0;
            }
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                int viewType = getItemViewType(position);
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                	if(0 == viewType){
                        v = vi.inflate(R.layout.list_element, parent, false);
                	} else {
                		v = vi.inflate(R.layout.list_element_navigation, parent, false);
                	}
                }
                
                if(1 == viewType){
                	// Set up the non-collection views
                    ImageView image = (ImageView) v.findViewById(R.id.navImageView);
                    TextView text = (TextView) v.findViewById(R.id.navTextView);
                    
                	int newPosition = position - this.numberOfCollections;
                	
                	switch(newPosition){
                	    case 0: // Add Collection
                	    	image.setBackgroundResource(R.drawable.icon_circle_add);
                	    	text.setText("New Collection");
                	    	break;
                	    case 1: // Remove Collection
                	    	image.setBackgroundResource(R.drawable.icon_minus);
                	    	text.setText("Delete Collection");
                	    	break;
                	    case 2: // Import Collections
                	    	image.setBackgroundResource(R.drawable.icon_cloud_upload);
                	    	text.setText("Import Collections");

                	    	break;
                	    case 3: // Export Collections
                	    	image.setBackgroundResource(R.drawable.icon_cloud_download);
                	    	text.setText("Export Collections");

                	        break;
						case 4: // Reorder Collections
							image.setBackgroundResource(R.drawable.icon_sort);
							text.setText("Reorder Collections");

							break;
                	    case 5: // Info
                	    	image.setBackgroundResource(R.drawable.icon_info);
                	    	text.setText("App Info");

                	    	break;
                	}
                	
                	return v;
                }
                
                // If it gets here, we need to set up a view for a collection
                
                CollectionListInfo item = items.get(position);
                
                String tableName = item.getName();
                
                int total = item.getCollected();
                if (tableName != null) {

                    ImageView image = (ImageView) v.findViewById(R.id.imageView1);
                    if (image != null) {
                        image.setBackgroundResource(item.getCoinReverseImageIdentifier());
                    }
                    
                    TextView tt = (TextView) v.findViewById(R.id.textView1);
                    if (tt != null) {
                        tt.setText(tableName);
                    }
                    
                    TextView mt = (TextView) v.findViewById(R.id.textView2);
                    if(mt != null){
                        mt.setText(total + "/" + item.getMax());
                    }
                    
                    TextView bt = (TextView) v.findViewById(R.id.textView3);
                    if(total >= item.getMax()){
                    	// The collection is complete
                    	if(bt != null){
                    		bt.setText("Collection Complete!");
                    	}
                    } else {
                    	bt.setText("");
                    }
                }
                return v;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);

    	// Note that this provides information about global focus state, which is managed
        // independently of activity lifecycles. As such, while focus changes will generally have
        // some relation to lifecycle changes (an activity that is stopped will not generally get
        // window focus), you should not rely on any particular order between the callbacks here
    	// and those in the other lifecycle methods such as onResume().

        // We use this function as a convenience for updating the database once the list gets focus
        // after returning from the add/delete/reorder views.
        
        if(hasFocus && mDatabaseHasBeenOpened && !mIsImportingCollection){
        	
        	// Only do this if the database has been opened with the AsyncTask first
        	// and we aren't modifying the database like crazy (importing)
            // We need this so that new collections that are added/removed get shown

            updateCollectionListFromDatabase();

            // Change it out with the new list
            mListAdapter.items = mCollectionListEntries;
            mListAdapter.numberOfCollections = mNumberOfCollections;
            mListAdapter.notifyDataSetChanged();
            
        }
    }

    /**
     * Reloads the collection list from the database.  This is useful after changes have been made
     * (collections reordered, deleted, etc.)
     */
    private void updateCollectionListFromDatabase(){

        // Get rid of the other items in the list (if any)
        mCollectionListEntries.clear();

        // Open the database.  The "big" open should have been called already.
        mDbAdapter.open();

        //Get a list of all the database tables
        Cursor resultCursor = mDbAdapter.getAllTables();

        if (resultCursor.moveToFirst()){
            do{
                CollectionListInfo listEntry = new CollectionListInfo();
                String name = resultCursor.getString(resultCursor.getColumnIndex("name"));
                listEntry.setName(name);
                listEntry.setType(resultCursor.getString(resultCursor.getColumnIndex("coinType")));
                listEntry.setMax(resultCursor.getInt(resultCursor.getColumnIndex("total")));
                listEntry.setCollected(mDbAdapter.fetchTotalCollected(name));

                // We don't save off the image to use, so figure out what that should be
                // TODO Not the best way to do this, find a better one
                int index = 0;
                for(int j = 0; j < mTypesOfCoins.length; j++){
                    if(mTypesOfCoins[j].equals(listEntry.getType())){
                        index = j;
                        break;
                    }
                }
                // TODO Consider adding error check in case we didn't find the name in typeOfCoins... This is pretty unlikely, though

                // Thanks! http://steven.bitsetters.com/2007/11/27/accessing-android-resources-by-name-at-runtime/
                int reverseIdentifier = getResources().getIdentifier(mReverseCoinImages[index], "drawable", getPackageName());
                listEntry.setCoinReverseImageIdentifier(reverseIdentifier);

                int obverseIdentifier = getResources().getIdentifier(mObverseCoinImages[index], "drawable", getPackageName());
                listEntry.setCoinObverseImageIdentifier(obverseIdentifier);

                // Add it to the list of collections
                mCollectionListEntries.add(listEntry);

            }while(resultCursor.moveToNext());
        }
        resultCursor.close();

        mDbAdapter.close();

        // Record the actual number of collections before spacers are added
        mNumberOfCollections = mCollectionListEntries.size();

        // We use an ArrayAdapter to power the ListView, but since we want to add in somethings that
        // don't have items in the list, we add in some blank entries to account for them.  Pretty
        // hacked together but it should work.
        for(int i = 0; i < NUMBER_OF_COLLECTION_LIST_SPACERS; i++) {
            mCollectionListEntries.add(null);
        }
    }
    
    private void showExportConfirmation(){
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Existing collections found on the external storage.  Exporting again will overwrite these.  Are you sure you want to do this?")
    	       .setCancelable(false)
    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   // TODO Maybe use AsyncTask, if necessary
                        handleExportCollectionsPart2();
    	           }
    	       })
    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    }

    private void showImportConfirmation(){
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Importing new collections will delete all existing collections in the App.  Are you sure you want to do this?")
    	       .setCancelable(false)
    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	               // Finish the import by kicking off an AsyncTask to do the heavy lifting
    	    		   mTask = new InitTask();

    	    		   mTask.doImport = true;
                       mTask.activity = MainActivity.this;
    	    		    
    	    		   MainActivity.this.mIsImportingCollection = true;
    	    		    
    	    		   mTask.execute();
    	           }
    	       })
    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	                handleImportCollectionsCancel();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
    private void showDeleteConfirmation(String name){

        // TODO Not sure why we have to do this????  Take out and ensure no breakage.  Maybe when I
        // originally wrote this my Java skills were just poor ;)
    	final String name2 = name;

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Are you sure you want to delete collection named '" + name2 + "'?")
    	       .setCancelable(false)
    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	               //Do the deleting
    	    		   mDbAdapter.open();
    	        	   mDbAdapter.dropTable(name2);

                       //Get a list of all the database tables
                       Cursor resultCursor = mDbAdapter.getAllCollectionNames();
                       int i = 0;
                       if (resultCursor.moveToFirst()){
                           do{
                               String name = resultCursor.getString(resultCursor.getColumnIndex("name"));
                               // Fix up the displayOrder
                               mDbAdapter.updateDisplayOrder(name,i);
                               i++;
                           }while(resultCursor.moveToNext());
                       }
                       resultCursor.close();

    	        	   mDbAdapter.close();
    	           }
    	       })
    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    }

    /**
     * Used by the ReorderCollections fragment to get our context
     * TODO Better way for it to get 'this'?
     * @return our Activity context
     */
    public Context getContext(){
    	return this;
    }

	// https://raw.github.com/commonsguy/cw-android/master/Rotation/RotationAsync/src/com/commonsware/android/rotation/async/RotationAsync.java
	// TODO Consider only using one of onSaveInstanceState and onRetainNonConfigurationInstanceState
	// TODO Also, read the notes on this better and make sure we are using it correctly
	@Override
	public Object onRetainCustomNonConfigurationInstance(){

		if(mProgressDialog != null && mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
			return mTask;
		} else {
			// No dialog showing, do nothing
			return null;
		}
	}

    @Override
    public void onDestroy(){

        // TODO Not a perfect solution, but assuming this gets called, we should cut down on the
        // race condition inherent in how we do our AsyncTask
        if(mTask != null) {
            mTask.activity = null;
        }
        super.onDestroy();
    }

	/**
	 * sub-class of AsyncTask
	 * Example from http://code.google.com/p/makemachine/source/browse/trunk/android/examples/async_task/src/makemachine/android/examples/async/AsyncTaskExample.java
	 */

	// We need to use this AsyncTask for two purposes - opening the database and also
	// importing a collection.

    // TODO For passing the AsyncTask between Activity instances, see this post:
    // http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
    // Our method is subject to the race conditions described therein :O

    class InitTask extends AsyncTask<Void, Void, Void>
	{
		// Use these to know whether we are opening the database or importing new ones
		boolean doImport = false;
		final String importMessage = "Importing Collections...";

		boolean doOpen = false;
		final String openMessage = "Opening Databases...";

        MainActivity activity;

		@Override
		protected Void doInBackground( Void... params )
		{
            if(activity == null){
                return null;
            }

			if(doOpen){

				DatabaseAdapter dbAdapter = new DatabaseAdapter(activity);
				dbAdapter.open();
				// Now just close it, since the database will have updated and
				// that's really all we need this AsyncTask for
				dbAdapter.close();

			} else if(doImport) {

				// Start doing all of the file reading and database importing
				activity.handleImportCollectionsPart2();
			}

			return null;
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			String message = openMessage;

			if(doImport){
				message = importMessage;
			}

            if(activity == null) {
                return;
            }

            activity.mProgressDialog = new ProgressDialog(activity);
            activity.mProgressDialog.setCancelable(false);
            activity.mProgressDialog.setMessage(message);
            activity.mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            activity.mProgressDialog.setProgress(0);
            activity.mProgressDialog.show();

		}

		@Override
		protected void onProgressUpdate(Void... values)
		{
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute( Void result )
		{
			super.onPostExecute(result);

            if(activity == null) {
                return;
            }

            if(activity.mProgressDialog.isShowing()){
                activity.mProgressDialog.dismiss();
            }
            activity.mProgressDialog = null;

            if(doOpen){

                activity.mDatabaseHasBeenOpened = true;

                activity.finishViewSetup();

            } else if(doImport) {

                activity.mIsImportingCollection = false;

                // If we've rotated and need to finish setting up the view, do it
                if(activity.mShouldFinishViewSetupToo){
                    activity.finishViewSetup();
                    activity.mShouldFinishViewSetupToo = false;
                }

                // Good to go!
            }
			doOpen = false;
			doImport = false;
            activity = null;
		}
	}

    /**
     * Takes the reordered list of collections in from the ReorderCollections fragment and updates
     * the ordering in the database.
     *
     * @param reorderedList The reordered list of collections
     */
	public void handleCollectionsReordered(ArrayList<CollectionListInfo> reorderedList){

        mDbAdapter.open();
        for(int i = 0; i < reorderedList.size(); i++){
            CollectionListInfo info = reorderedList.get(i);
            mDbAdapter.updateDisplayOrder(info.getName(),i);
            mCollectionListEntries.set(i, info);
        }
        mDbAdapter.close();
    }
}