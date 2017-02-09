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
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType; 

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
	private ObservableList<String> songs;
	private ArrayList<Song> songList;
	//private ArrayList<String> list;
	int itemIndex;
	
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
	
	/**
	 * Add listeners to buttons and listview items, initialize data structures, load data from file, and set up display
	 */
	@Override	
	public void initialize(URL location, ResourceBundle resources) {	
		songList = new ArrayList<Song>();
		ArrayList<String> songs = new ArrayList<>();
		loadFile();
		loadListData();
		
		//Add listener so we can display song details when a user selects a song from the list
		listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	itemIndex = listView.getSelectionModel().getSelectedIndex();
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
				if (editBtn.getText().equals("Edit")) {
					editBtn.setText("Submit");
					setupEdit();
				} else {
					editBtn.setText("Edit");
					handleEdit(listView.getSelectionModel().getSelectedIndex());
				}
		    	
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
	public void handleEdit(int index) {
		Song song = songList.get(index);
		String oldName = song.getName();
		String oldArtist = song.getArtist();
		String songName = songNameTxt.getText();
		String artist = artistTxt.getText();
		String album = albumTxt.getText();
		String year = yearTxt.getText();
		if (album == null) {
			album = "";
		}
		if (year == null) {
			year = "";
		}
		song.setAlbum(album);
		song.setYear(year);
		Song tempSong = new Song();
		tempSong.setArtist(artist);
		tempSong.setName(songName);
		String context = "Song name: " + songName + "\nArtist: " + artist + "\nAlbum: " + album + "\nYear: " + year;
		if (songName == null || songName.equals("") || artist == null || artist.equals("")) {
			//Error prompt
			songNameTxt.setText("");
			artistTxt.setText("");
			albumTxt.setText("");
			yearTxt.setText("");
			return;
		} else if (!oldName.equals(songName)) {
			//Song name changed
			if (!oldArtist.equals(artist)) {
				if (songExists(tempSong)) {
					//prompt error
					existenceError();
					return;
				} else {
					//Song doesn't exist, add to list
					if (confirmChanges(context)) {
						song.setArtist(artist);
						song.setName(songName);
						listView.getItems().remove(index);
						songList.remove(index);
						insertInOrder(song);
						saveFile();
					}
				}
			} else {
				if (songExists(tempSong)) {
					//prompt error
					existenceError();
					return;
				} else {
					//Song doesn't exist, add to list
					if (confirmChanges(context)) {
						song.setName(songName);
						listView.getItems().remove(index);
						songList.remove(index);
						insertInOrder(song);
						saveFile();
					}
				}
			}
		} else if (!oldArtist.equals(artist)) {
			//Artist name changed, but song name did not
			if (songExists(tempSong)) {
				//prompt error
				existenceError();
				return;
			} else {
				//Song doesn't exist, add to list
				if (confirmChanges(context)) {
					song.setArtist(artist);
					songList.set(index, song);
					saveFile();
				}
			}
		} else {
			//Song name and artist did not change
			songList.set(index, song);
			saveFile();
		}
		songNameTxt.setText("");
		artistTxt.setText("");
		albumTxt.setText("");
		yearTxt.setText("");
		displayItemDetails(itemIndex);
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
		String context = "Song name: " + songName + "\nArtist: " + artist + "\nAlbum: " + album + "\nYear: " + year;
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
				existenceError();
				return;
			} else {
				if (confirmChanges(context)) {
					insertInOrder(song);
					saveFile();
				}
			}
		}
		songNameTxt.setText("");
		artistTxt.setText("");
		albumTxt.setText("");
		yearTxt.setText("");
	}
	
	/**
	 * Return true if song exists in our dataset, false otherwise
	 * @param song
	 * @return
	 */
	public boolean songExists(Song song) {
		for (Song s : songList) {
			if (s.getName().equals(song.getName()) && s.getArtist().equals(song.getArtist())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Fill edit fields with song details so user knows what to change
	 */
	public void setupEdit() {
		Song song = songList.get(itemIndex);
		songNameTxt.setText(song.getName());
		albumTxt.setText(song.getAlbum());
		artistTxt.setText(song.getArtist());
		yearTxt.setText(song.getYear());
	}
	
	/**
	 * Display song details about the selected song
	 * @param index
	 */
	public void displayItemDetails(int index) {
		Song song = songList.get(index);
		songLabel.setText(song.getName());
		artistLabel.setText(song.getArtist());
		if (song.getAlbum() != null) {
			albumLabel.setText(song.getAlbum());
		} else {
			albumLabel.setText("");
		}
		if (song.getYear() != null) {
			yearLabel.setText(song.getYear());
		} else {
			yearLabel.setText("");
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
		songList.add(index, song);
	}
	
	/**
	 * Load data from the map into an array list and sort it.
	 * Then copy this list to our observable list and add it
	 * to our listview.
	 */
	private void loadListData() {
		songs = FXCollections.observableArrayList();
		ArrayList<String> list = new ArrayList<>();
		for (Song song : songList) {
			list.add(song.getName());
		}
		Collections.sort(list);
		songs.addAll(list);
		listView.getItems().addAll(songs);
	}
	
	/**
	 * Return user validation
	 */
	private boolean confirmChanges(String context) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm Changes");
		alert.setHeaderText("Are you sure you want to apply these changes?");
		alert.setContentText(context);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
		    // ok was pressed.
			return true;
		} else {
		    // cancel might have been pressed.
			return false;
		}
	}
	
	/**
	 * Display error message for when user-artist combo exists already
	 */
	private void existenceError() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Could not apply change.");
		alert.setContentText("This song already exists!");
		alert.showAndWait();
		songNameTxt.setText("");
		artistTxt.setText("");
		albumTxt.setText("");
		yearTxt.setText("");
	}
	
	/**
	 * Save data to a file
	 */
	private void saveFile() {
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(new FileOutputStream("songlib/data.ser"));
			out.writeObject(songList);
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
				songList = (ArrayList<Song>) ois.readObject();
				ois.close();
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
