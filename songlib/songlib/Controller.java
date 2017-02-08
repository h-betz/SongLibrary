package songlib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Class to handle application logic
 * @author hbetz
 *
 */
public class Controller implements javafx.fxml.Initializable {
	
	/**
	 * Controller variables such as data structures and GUI components
	 */
	private HashMap<String, Song> map;
	private HashMap<Integer, Song> mapping;
	private ObservableList<String> songs;
	private ArrayList<String> list;
	
	@FXML
	private Button addBtn;
	@FXML
	private Button deleteBtn;
	@FXML
	private Button editBtn;
	@FXML
	private TextField songNameTxt;
	@FXML
	private TextField artistTxt;
	@FXML
	private TextField albumTxt;
	@FXML
	private TextField yearTxt;
	@FXML
	private ListView<String> listView;
	@FXML
	private Label songLabel;
	@FXML
	private Label artistLabel;
	@FXML
	private Label albumLabel;
	@FXML
	private Label yearLabel;
	
	
	@Override	
	public void initialize(URL location, ResourceBundle resources) {	
		map = new HashMap<String, Song>();
		mapping = new HashMap<Integer, Song>();
		ArrayList<String> songs = new ArrayList<>();
		loadFile();
		loadListData();
		
		//Add listener so we can display song details when a user selects a song from the list
		listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        int index = listView.getSelectionModel().getSelectedIndex();
		        displayItemDetails(index);
		    }
		});
		
		//Add listener to handle user song additions
		addBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
		    public void handle(ActionEvent e) {
		    	handleAdd();
		    }
		});
		
		//Add a listener to handle song deletions
		deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
		    public void handle(ActionEvent e) {
		    	handleDelete();
		    }
		});
		
		//Add a listener to handle song edits
		editBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
		    public void handle(ActionEvent e) {
		    	handleEdit();
		    }
		});
	}

	/**
	 * Method to handle song deletion
	 */
	public void handleDelete() {
		
	}
	
	/**
	 * Method to handle song edits
	 */
	public void handleEdit() {
		
	}
	
	/**
	 * Method to handle adding songs to the list
	 */
	public void handleAdd() {
		
		//Get data from textfields
		String songName = songNameTxt.getText();
		String artist = artistTxt.getText();
		String album = albumTxt.getText();
		String year = yearTxt.getText();
		
		//Add songs to list as long as name and artist are provided and as long as song doesn't already exist
		if (songName == null || songName.equals("") || artist == null || artist.equals("")) {
			//Error prompt
			return;
		} else {
			Song song = new Song();
			song.setName(songName);
			song.setArtist(artist);
			if (album != null && !album.equals("")) {
				song.setAlbum(album);
			}
			if (year != null && !year.equals("")) {
				song.setYear(year);
			}
			if (songExists(song)) {
				//Song and artist combo already exist, prompt error message
				return;
			} else {
				map.put(song.getName()+song.getArtist(), song);
				insertInOrder(song);
				saveFile();
			}
			
		}
	}
	
	/**
	 * Return true if song exists in our dataset, false otherwise
	 * @param song
	 * @return
	 */
	public boolean songExists(Song song) {
		for (Map.Entry<Integer, Song> entry : mapping.entrySet()) {
			Song s = entry.getValue();
			if (s.getName().equals(song.getName()) && s.getArtist().equals(song.getArtist())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Display song details about the selected song
	 * @param index
	 */
	public void displayItemDetails(int index) {
		Song song = mapping.get(index);
		songLabel.setText(song.getName());
		artistLabel.setText(song.getArtist());
		if (song.getAlbum() != null) {
			albumLabel.setText(song.getAlbum());
		}
		if (song.getYear() != null) {
			yearLabel.setText(song.getYear());
		}
	}
	
	/**
	 * Insert new song in alphabetical order
	 * @param song
	 */
	private void insertInOrder(Song song) {
		String name = song.getName();
		int index = 0;
		ObservableList<String> items = listView.getItems();
		for (String s : items) {
			int result = name.compareToIgnoreCase(s);
			if (result < 0) {
				break;
			}
			index++;
		}
		listView.getItems().add(index, name);
		list.add(index, name);
		mapping.put(index, song);
	}
	
	/**
	 * Load data from the map into an array list and sort it.
	 * Then copy this list to our observable list and add it
	 * to our listview.
	 */
	private void loadListData() {
		songs = FXCollections.observableArrayList();
		list = new ArrayList<>();
		for (Entry<Integer, Song> entry : mapping.entrySet()) {
			Song song = entry.getValue();
			list.add(song.getName());
		}
		Collections.sort(list);
		songs.addAll(list);
		listView.getItems().addAll(songs);
	}
	
	/**
	 * Save data to a file
	 */
	private void saveFile() {
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(new FileOutputStream("songlib/data.ser"));
			out.writeObject(mapping);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Load data file
	 */
	private void loadFile() {
		File f = new File("songlib/data.ser");
		if(f.exists() && !f.isDirectory()) { 
			try {
				FileInputStream fis = new FileInputStream("songlib/data.ser");
				ObjectInputStream ois;
				ois = new ObjectInputStream(fis);
				mapping = (HashMap<Integer, Song>) ois.readObject();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return;
		}
		
		
	}
}
