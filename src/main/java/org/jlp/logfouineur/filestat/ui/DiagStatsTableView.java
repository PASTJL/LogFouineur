/*
 * Copyright 2017 Jean-Louis Pasturel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
*/
package org.jlp.logfouineur.filestat.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map.Entry;
import java.util.Set;

import org.jlp.javafx.richview.RowTableModel;
import org.jlp.logfouineur.filestat.models.CumulEnregistrementStat;
import org.jlp.logfouineur.filestat.models.RowTableFileStats;
import org.jlp.logfouineur.ui.LogFouineurMain;
import org.jlp.logfouineur.util.MyDoubleFormatter;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// TODO: Auto-generated Javadoc
/**
 * The Class DiagStatsTableView.
 */
public class DiagStatsTableView extends Stage {

	/** The table view. */
	public TableView<RowTableFileStats> tableView = new TableView<RowTableFileStats>();

	/** The num row. */
	public static TableColumn<RowTableFileStats, Integer> numRow = new TableColumn<>("NumRow");

	/** The criteria. */
	private static TableColumn<RowTableFileStats, String> criteria = new TableColumn<>("Criteria");

	/** The count. */
	private static TableColumn<RowTableFileStats, Integer> count = new TableColumn<>("Count");

	/** The per cent. */
	private static TableColumn<RowTableFileStats, Double> perCent = new TableColumn<>("PerCent");

	/** The sum. */
	private static TableColumn<RowTableFileStats, Double> sum = new TableColumn<>("Sum");

	/** The average. */
	private static TableColumn<RowTableFileStats, Double> average = new TableColumn<>("Average");

	/** The minimum. */
	private static TableColumn<RowTableFileStats, Double> minimum = new TableColumn<>("Minimum");

	/** The maximum. */
	private static TableColumn<RowTableFileStats, Double> maximum = new TableColumn<>("Maximum");

	/** The mediane. */
	private static TableColumn<RowTableFileStats, Double> mediane = new TableColumn<>("Mediane");

	/** The percentile. */
	private static TableColumn<RowTableFileStats, Double> percentile = new TableColumn<>(
			"Percentile " + DiagFileStats.tfPerCentile.getText());

	/** The std dev. */
	private static TableColumn<RowTableFileStats, Double> stdDev = new TableColumn<>("stdDev");

	/** The content pane. */
	VBox contentPane = new VBox();
	
	/** The scene. */
	public static Scene scene;
	
	/** The dim screen. */
	public static Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
	
	/** The initial list. */
	ObservableList<RowTableFileStats> initialList;

	/**
	 * Instantiates a new diag stats table view.
	 */
	public DiagStatsTableView() {
		super();
		getIcons().add(new Image("/images/lflogo.png"));
		setTitle("TableView for file "+DiagFileStats.fileName+ ". Right clic on table to export to a csv file");
		constructTableColumns();

		numRow.setId("numRow");
		criteria.setId("criteria");

		count.setId("count");
		perCent.setId("perCent");
		sum.setId("sum");

		average.setId("average");
		minimum.setId("minimum");
		maximum.setId("maximum");
		mediane.setId("mediane");
		percentile.setId("percentile");

		stdDev.setId("stdDev");

		tableView.setMinWidth(dimScreen.getWidth() * 3 / 4);
		tableView.setMinHeight(dimScreen.getHeight() * 3 / 4);
		contentPane.getChildren().add(tableView);
		scene = new javafx.scene.Scene(contentPane, dimScreen.getWidth() * 3 / 4, dimScreen.getHeight() * 3 / 4);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		this.setScene(scene);

		this.initModality(Modality.APPLICATION_MODAL);

		fillTable();
		initialList = tableView.getItems();

		tableView.setOnMouseClicked(event -> {

			// Step 2: Get the height of the header.
			double headerHeight = tableView.lookup(".column-header-background").getBoundsInLocal().getHeight();

			// Step 3: Check if the clicked position's Y value is less than or equal to the
			// height of the header.
			if (headerHeight >= event.getY()) {
				// Clicked on the header!
				System.out.println("Clicked in header");

			} else {
			
				if (event.getButton()==MouseButton.SECONDARY) {
					
					final ContextMenu cm = new ContextMenu();
					MenuItem cmItem1 = new MenuItem("Export first " + DiagFileStats.topN+ " lines of table");
					cmItem1.setOnAction(new EventHandler<ActionEvent>() {
					    public void handle(ActionEvent e) {
					       System.out.println("Exporting Table view");
					       exportTable(false);
					    }
					});
					MenuItem cmItem2 = new MenuItem("Export all lines of table");
					cmItem2.setOnAction(new EventHandler<ActionEvent>() {
					    public void handle(ActionEvent e) {
					       
					       exportTable(true);
					    }
					});
					cm.getItems().addAll(cmItem1,cmItem2);
					cm.show(this, event.getScreenX(), event.getScreenY());
					
				}
				
			}
		});
		this.showAndWait();

	}

	/**
	 * Export table.
	 *
	 * @param forAll the for all
	 */
	protected void exportTable(boolean forAll) {
		// create String for title
		String title= "";
		for (int i=0;i< tableView.getColumns().size();i++) {
			title+=tableView.getColumns().get(i).getText()+DiagFileStats.tfCsvSep.getText();
		}
		String sep=DiagFileStats.tfCsvSep.getText();
		title+="\n";
		// create file
		String strFile=LogFouineurMain.workspace+File.separator+LogFouineurMain.currentProject+File.separator+LogFouineurMain.currentScenario+
				File.separator+"logs"+File.separator+DiagFileStats.fileName+".csv";
		System.out.println("Saving in : "+strFile);
		if (new File(strFile).exists())new File(strFile).delete();
		int nbLines=0;
		if(forAll) {
			nbLines=tableView.getItems().size();
		}else
		{
			nbLines=Math.min(tableView.getItems().size(), DiagFileStats.topN);
		}
		MyDoubleFormatter myFrm=new MyDoubleFormatter();
		RandomAccessFile raf=null;
		try {
			raf=new RandomAccessFile(strFile,"rw");
			raf.writeBytes(title);
			ObservableList<RowTableFileStats> list=tableView.getItems();
			String line="";
			for (int i=0; i<nbLines;i++) {
				RowTableFileStats row=list.get(i);
				line=""+row.numRow+sep+row.criteria+sep+row.count+sep+myFrm.toString(row.perCent).trim()+sep+myFrm.toString(row.sum).trim()+sep+
						myFrm.toString(row.average).trim()+sep+myFrm.toString(row.minimum).trim()+
						sep+myFrm.toString(row.maximum).trim()+sep+myFrm.toString(row.mediane).trim()
						+sep+myFrm.toString(row.percentile).trim()+sep+myFrm.toString(row.stdDev).trim()+sep+"\n";
				raf.writeBytes(line);
				line="";
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				raf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Fill table.
	 */
	private void fillTable() {
		tableView.setEditable(true);
		Set<Entry<String, CumulEnregistrementStat>> entrySet = DiagFileStats.allHmCumul.entrySet();
		// System.out.println("entrySet.size="+entrySet.size());
		ObservableList<RowTableFileStats> list = tableView.getItems();
		int i = 0;
		int totalCount = 0;
		for (Entry<String, CumulEnregistrementStat> entry : entrySet) {
			totalCount += entry.getValue().rowflstats.count;
		}
		System.out.println("totalCount=" + totalCount);
		for (Entry<String, CumulEnregistrementStat> entry : entrySet) {
			RowTableFileStats row = entry.getValue().rowflstats;

			row.setPerCent((double) (((double) row.count) / ((double) totalCount)) * 100);
			row.numRow = i;
			// System.out.println("traitement="+entry.getValue().rowflstats.criteria+
			// ","+entry.getValue().rowflstats.getAverage());
			list.add(row);
			i++;
		}
		tableView.setItems(list);
		tableView.setEditable(false);
	}

	/**
	 * Construct table columns.
	 */
	private void constructTableColumns() {
		numRow.setCellValueFactory(new PropertyValueFactory<RowTableFileStats, Integer>("numRow"));
		numRow.setCellFactory(col -> {
			TableCell<RowTableFileStats, Integer> cell = new TableCell<RowTableFileStats, Integer>() {
				@Override
				public void updateItem(Integer item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);

					} else {
						setText(Integer.toString(item));
						setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

					}
				}
			};

			return cell;
		});
		numRow.setReorderable(false);
		criteria.setCellValueFactory(new PropertyValueFactory<RowTableFileStats, String>("criteria"));
		criteria.setCellFactory(col -> {
			TableCell<RowTableFileStats, String> cell = new TableCell<RowTableFileStats, String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);

					} else {
						setText(item);
						setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

					}
				}
			};

			return cell;
		});
		criteria.setReorderable(false);
		count.setCellValueFactory(new PropertyValueFactory<RowTableFileStats, Integer>("count"));
		count.setCellFactory(col -> {

			TableCell<RowTableFileStats, Integer> cell = new TableCell<RowTableFileStats, Integer>() {
				@Override
				public void updateItem(Integer item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);

					} else {
						setText(Integer.toString(item));
						setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

					}
				}
			};

			return cell;
		});

		perCent.setCellValueFactory(new PropertyValueFactory<RowTableFileStats, Double>("perCent"));
		perCent.setCellFactory(col -> {
			TableCell<RowTableFileStats, Double> cell = new TableCell<RowTableFileStats, Double>() {
				@Override
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);

					} else {
						setText(new MyDoubleFormatter().toString(item));
						setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

					}
				}
			};

			return cell;
		});
		perCent.setReorderable(false);
		sum.setCellValueFactory(new PropertyValueFactory<RowTableFileStats, Double>("sum"));
		sum.setCellFactory(col -> {
			TableCell<RowTableFileStats, Double> cell = new TableCell<RowTableFileStats, Double>() {
				@Override
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);

					} else {
						setText(new MyDoubleFormatter().toString(item));
						setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

					}
				}
			};

			return cell;
		});
		sum.setReorderable(false);
		average.setCellValueFactory(new PropertyValueFactory<RowTableFileStats, Double>("average"));
		average.setCellFactory(col -> {
			TableCell<RowTableFileStats, Double> cell = new TableCell<RowTableFileStats, Double>() {
				@Override
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);

					} else {
						setText(new MyDoubleFormatter().toString(item));
						setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

					}
				}
			};

			return cell;
		});
		average.setReorderable(false);
		minimum.setCellValueFactory(new PropertyValueFactory<RowTableFileStats, Double>("minimum"));
		minimum.setCellFactory(col -> {
			TableCell<RowTableFileStats, Double> cell = new TableCell<RowTableFileStats, Double>() {
				@Override
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);

					} else {
						setText(new MyDoubleFormatter().toString(item));
						setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

					}
				}
			};

			return cell;
		});
		minimum.setReorderable(false);
		maximum.setCellValueFactory(new PropertyValueFactory<RowTableFileStats, Double>("maximum"));
		maximum.setCellFactory(col -> {
			TableCell<RowTableFileStats, Double> cell = new TableCell<RowTableFileStats, Double>() {
				@Override
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);

					} else {
						setText(new MyDoubleFormatter().toString(item));
						setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

					}
				}
			};

			return cell;
		});
		maximum.setReorderable(false);
		mediane.setCellValueFactory(new PropertyValueFactory<RowTableFileStats, Double>("mediane"));
		mediane.setCellFactory(col -> {
			TableCell<RowTableFileStats, Double> cell = new TableCell<RowTableFileStats, Double>() {
				@Override
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);

					} else {
						setText(new MyDoubleFormatter().toString(item));
						setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

					}
				}
			};

			return cell;
		});
		mediane.setReorderable(false);
		percentile.setCellValueFactory(new PropertyValueFactory<RowTableFileStats, Double>("percentile"));
		percentile.setCellFactory(col -> {
			TableCell<RowTableFileStats, Double> cell = new TableCell<RowTableFileStats, Double>() {
				@Override
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);

					} else {
						setText(new MyDoubleFormatter().toString(item));
						setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

					}
				}
			};

			return cell;
		});
		percentile.setReorderable(false);
		stdDev.setCellValueFactory(new PropertyValueFactory<RowTableFileStats, Double>("stdDev"));
		stdDev.setCellFactory(col -> {
			TableCell<RowTableFileStats, Double> cell = new TableCell<RowTableFileStats, Double>() {
				@Override
				public void updateItem(Double item, boolean empty) {
					if (empty || item == null) {
						setGraphic(null);

					} else {
						setText(new MyDoubleFormatter().toString(item));
						setStyle("-fx-font-weight:bold;-fx-font-size:12px;");

					}
				}
			};

			return cell;
		});
		stdDev.setReorderable(false);
		tableView.getColumns().addAll(numRow, criteria, count, perCent, sum, average, minimum, maximum, mediane,
				percentile, stdDev);

	}

}
