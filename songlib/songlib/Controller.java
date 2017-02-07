package songlib;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
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
	
	
	@Override	
	public void initialize(URL location, ResourceBundle resources) {	
		map = new HashMap<String, Song>();
		ArrayList<String> songs = new ArrayList<>();
		loadListData();
		//listView.getItems().addAll("Iron Man", "Captain America");
		
		addBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
		    public void handle(ActionEvent e) {
		    	handleAdd();
		    }
		});
		
		deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
		    public void handle(ActionEvent e) {
		    	handleDelete();
		    }
		});
		
		editBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
		    public void handle(ActionEvent e) {
		    	handleEdit();
		    }
		});
	}

	public void handleDelete() {
		
	}
	
	public void handleEdit() {
		
	}
	
	/**
	 * Class to handle adding songs to the list
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
			if (map.containsKey(song.getName()+song.getArtist())) {
				//Song and artist combo already exist, prompt error message
			} else {
				map.put(song.getName()+song.getArtist(), song);
			}
			listView.getItems().add(songName);
		}
	}
	
	/**
	 * Load data from the map into an array list and sort it.
	 * Then copy this list to our observable list and add it
	 * to our listview.
	 */
	public void loadListData() {
		songs = FXCollections.observableArrayList();
		list = new ArrayList<>();
		list.add("Madness");
		list.add("Startlight");
		list.add("Numb");
		list.add("Hallelujah");
		list.add("All These Things");
		Collections.sort(list);
		songs.addAll(list);
		listView.getItems().addAll(songs);
	}
}
