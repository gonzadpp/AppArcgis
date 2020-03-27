/**
 * Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.mycompany.app;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.Graphic;
import java.net.MalformedURLException;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler;
import com.esri.arcgisruntime.security.OAuthConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.tasks.networkanalysis.Route;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;

public class App<actionEvent> extends Application {

    private MapView mapView;
    private int hexRed = 0xFFFF0000;
    private int hexBlue = 0xFF00FF00;
    private int hexGreen = 0xFF0000FF;
    private GraphicsOverlay graphicsOverlay;
    private Point startPoint;
    private Point endPoint;
    private final int hexRed1 = 0xFFFF0000;
    private final int hexBlue1 = 0xFF0000FF;
    private RouteTask solveRouteTask;
    private RouteParameters routeParameters;
    private Button btnRouteLogger, btnSaveRoute, btnDrawRoute;
    private Stage stageRouteLogger;
    private Scene sceneRouteLogger;
    private ListView<com.mycompany.app.Route> listViewLogger;
    //private MapView mapView;    
    private void setupGraphicsOverlay() {
    	  if (mapView != null) {
    	    graphicsOverlay = new GraphicsOverlay();
    	    mapView.getGraphicsOverlays().add(graphicsOverlay);
    	  }
    	}
    private void addPointGraphic() {
    	  if (graphicsOverlay != null) {
    	    SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, hexRed, 10.0f);
    	    pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f));
    	    Point point = new Point(-98.198176, 19.043772, SpatialReferences.getWgs84());
    	    Graphic pointGraphic = new Graphic(point, pointSymbol);
    	    graphicsOverlay.getGraphics().add(pointGraphic);
    	  }
    	}
    private void addPolylineGraphic() {
    	  if (graphicsOverlay != null) {
    	    PointCollection polylinePoints = new PointCollection(SpatialReferences.getWgs84());
    	    polylinePoints.add(new Point(-118.29026, 34.1816));
    	    polylinePoints.add(new Point(-118.26451, 34.09664));
    	    Polyline polyline = new Polyline(polylinePoints);
    	    SimpleLineSymbol polylineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 3.0f);
    	    Graphic polylineGraphic = new Graphic(polyline, polylineSymbol);
    	    graphicsOverlay.getGraphics().add(polylineGraphic);
    	  }
    	}
    private void addPolygonGraphic() {
    	  if (graphicsOverlay != null) {
    	    PointCollection polygonPoints = new PointCollection(SpatialReferences.getWgs84());
    	    polygonPoints.add(new Point(-118.27653, 34.15121));
    	    polygonPoints.add(new Point(-118.24460, 34.15462));
    	    polygonPoints.add(new Point(-118.22915, 34.14439));
    	    polygonPoints.add(new Point(-118.23327, 34.12279));
    	    polygonPoints.add(new Point(-118.25318, 34.10972));
    	    polygonPoints.add(new Point(-118.26486, 34.11625));
    	    polygonPoints.add(new Point(-118.27653, 34.15121));
    	    Polygon polygon = new Polygon(polygonPoints);
    	    SimpleFillSymbol polygonSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, hexGreen,
    	      new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f));
    	    Graphic polygonGraphic = new Graphic(polygon, polygonSymbol);
    	    graphicsOverlay.getGraphics().add(polygonGraphic);
    	  }
    	}
    private void setupAuthentication() {
        String portalURL = "https://www.arcgis.com";
        String clientId = "yWVOZyPDBdoao5CT";
        String redirectURI = "urn:ietf:wg:oauth:2.0:oob";
        try {
            OAuthConfiguration oAuthConfiguration = new OAuthConfiguration(portalURL, clientId, redirectURI);
            AuthenticationManager.setAuthenticationChallengeHandler(new DefaultAuthenticationChallengeHandler());
            AuthenticationManager.addOAuthConfiguration(oAuthConfiguration);
            final Portal portal = new Portal(portalURL, true);
            portal.addDoneLoadingListener(() -> {
              if (portal.getLoadStatus() == LoadStatus.LOADED) {
            	  String routeServiceURI = "https://route.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World";
            	  setupRouteTask(routeServiceURI);
            	  addTrafficLayer(portal);
              } else {
                new Alert(Alert.AlertType.ERROR, "Portal: " + portal.getLoadError().getMessage()).show();
              }
            });
            portal.loadAsync();

          } catch (MalformedURLException e) {
            e.printStackTrace();
          }
    }
    private void addTrafficLayer(Portal portal) {
        String trafficURL = "https://traffic.arcgis.com/arcgis/rest/services/World/Traffic/MapServer";
        ArcGISMapImageLayer layer = new ArcGISMapImageLayer(trafficURL);
        layer.addDoneLoadingListener(() -> {
            if (portal.getLoadStatus() != LoadStatus.LOADED) {
                new Alert(Alert.AlertType.ERROR, "Layer: " + layer.getLoadError().getMessage()).show();
            }
        });
        mapView.getMap().getOperationalLayers().add(layer);
    }
    private void setupRouteTask(String routeServiceURI) {
    	   solveRouteTask = new RouteTask(routeServiceURI);
    	   solveRouteTask.loadAsync();
    	   solveRouteTask.addDoneLoadingListener(() -> {
    		   if (solveRouteTask.getLoadStatus() == LoadStatus.LOADED) {
    		     final ListenableFuture<RouteParameters> routeParamsFuture = solveRouteTask.createDefaultParametersAsync();
    		     routeParamsFuture.addDoneListener(() -> {

    		       try {
    		         routeParameters = routeParamsFuture.get();
    		         listeners();
    		       } catch (InterruptedException | ExecutionException e) {
    		         new Alert(Alert.AlertType.ERROR, "Cannot create RouteTask parameters " + e.getMessage()).show();
    		       }
    		     });
    		     createRouteAndDisplay();
    		   } else {
    		     new Alert(Alert.AlertType.ERROR, "Unable to load RouteTask " + solveRouteTask.getLoadStatus().toString()).show();
    		   }
    		 });
    	 }
    private void setMapMarker(Point location, SimpleMarkerSymbol.Style style, int markerColor, int outlineColor) {
    	   float markerSize = 8.0f;
    	   float markerOutlineThickness = 2.0f;
    	   SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(style, markerColor, markerSize);
    	   pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, outlineColor, markerOutlineThickness));
    	   Graphic pointGraphic = new Graphic(location, pointSymbol);
    	   graphicsOverlay.getGraphics().add(pointGraphic);
    	 }
    private void setStartMarker(Point location) {
    	   graphicsOverlay.getGraphics().clear();
    	   setMapMarker(location, SimpleMarkerSymbol.Style.DIAMOND, hexRed1, hexBlue1);
    	   startPoint = location;
    	   endPoint = null;
    	 }
    private void setEndMarker(Point location) {
    	   setMapMarker(location, SimpleMarkerSymbol.Style.SQUARE, hexBlue, hexRed);
    	   endPoint = location;
    	   solveForRoute();
    	 }
    private void createRouteAndDisplay() {
    	   mapView.setOnMouseClicked(e -> {
    	     if (e.getButton() == MouseButton.PRIMARY) {
    	       Point2D point = new Point2D(e.getX(), e.getY());
    	       Point mapPoint = mapView.screenToLocation(point);

    	       if (startPoint == null) {
    	         setStartMarker(mapPoint);
    	       } else if (endPoint == null) {
    	         setEndMarker(mapPoint);
    	       } else {
    	         setStartMarker(mapPoint);
    	       }
    	     }
    	   });
    	 }
    private void solveForRoute() {

    	   if (startPoint != null && endPoint != null) {
    	     routeParameters.setStops(Arrays.asList(new Stop(startPoint), new Stop(endPoint)));

    	     final ListenableFuture<RouteResult> routeResultFuture = solveRouteTask.solveRouteAsync(routeParameters);
    	     routeResultFuture.addDoneListener(() -> {
    	    	   try {
    	    	     RouteResult routeResult = routeResultFuture.get();
    	    	     if(routeResult.getRoutes().size() > 0) {
    	    	    	    Route firstRoute = routeResult.getRoutes().get(0);

    	    	    	    Polyline routePolyline = firstRoute.getRouteGeometry();
    	    	    	    SimpleLineSymbol routeSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 4.0f);
    	    	    	    Graphic routeGraphic = new Graphic(routePolyline, routeSymbol);
    	    	    	    graphicsOverlay.getGraphics().add(routeGraphic);
    	    	    	   } else {
    	    	    	    new Alert(Alert.AlertType.WARNING, "No routes have been found.").show();
    	    	    	  }
    	    	   } catch (InterruptedException | ExecutionException e) {
    	    	     new Alert(Alert.AlertType.ERROR, "Solve RouteTask failed " + e.getMessage() + e.getMessage()).show();
    	    	   }
    	    	 });
    	   }
    }  
    private void crearComponents() {    	
    	btnRouteLogger = new Button("Mis Rutas");
    	btnRouteLogger.setDefaultButton(true);
    	btnRouteLogger.autosize();
    	StackPane.setAlignment(btnRouteLogger, Pos.TOP_CENTER);
    	StackPane.setMargin(btnRouteLogger, new Insets(10, 10, 0, 0));
    }
    private void createLoggerRoute(Stage stage) {
		stageRouteLogger = new Stage();
		stageRouteLogger.setTitle("Registro de Ruutas");
		//Nodes
		btnSaveRoute = new Button("Guardar");
		btnDrawRoute = new Button("Dibujar");
		listViewLogger = new ListView<com.mycompany.app.Route>();
		ArrayList<com.mycompany.app.Route> list = new ArrayList<com.mycompany.app.Route>();
		ObservableList<com.mycompany.app.Route> routesCollection = FXCollections.observableArrayList(list);
		listViewLogger.setItems(routesCollection);
		listViewLogger.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20, 20, 20, 20));
		layout.getChildren().addAll(listViewLogger, btnSaveRoute, btnDrawRoute);
		sceneRouteLogger = new Scene(layout, 300,450);
		stageRouteLogger.setScene(sceneRouteLogger);
		btnSaveRoute.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				String nRoute = JOptionPane.showInputDialog("Nombre de la Ruta: ");
				com.mycompany.app.Route routeObj;
				routeObj = new com.mycompany.app.Route(nRoute, startPoint, endPoint);
				routesCollection.add(routeObj);
				listViewLogger.getSelectionModel().clearSelection();
			}
		});
		btnDrawRoute.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				String message = "";
				ObservableList<com.mycompany.app.Route> observableRoutes;
				observableRoutes = listViewLogger.getSelectionModel().getSelectedItems();
				for (com.mycompany.app.Route m: observableRoutes) {
					message += m + "\n";
				}
				System.out.println(message);
				com.mycompany.app.Route routeObj;
				routeObj = listViewLogger.getSelectionModel().getSelectedItem();
				setStartMarker(routeObj.getStartPoint());
				setEndMarker(routeObj.getEndPoint());
			}
		});
		
	}
    private void listeners() {
		javafx.event.EventHandler<ActionEvent> showHistory = new javafx.event.EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				if (stageRouteLogger.isShowing()) {
					stageRouteLogger.hide();
				} else {
					stageRouteLogger.show();
				}
			}
		};
		btnRouteLogger.addEventFilter(ActionEvent.ACTION, showHistory);
    }
    /*
     * 34.02700, -118.80543 montañas de Santa Mónica en California
     * 19.043772, -98.198176 centro de Puebla
     */
    private void setupMap() {
       if (mapView != null) {
           Basemap.Type basemapType = Basemap.Type.STREETS_WITH_RELIEF_VECTOR;
           double latitude = 19.043772; 
           double longitude = -98.198176;
           int levelOfDetail = 6;
           ArcGISMap map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);
           mapView.setMap(map);
       }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {

        // set the title and size of the stage and show it
        stage.setTitle("My Map App");
        stage.setWidth(800);
        stage.setHeight(700);
        stage.show();

        // create a JavaFX scene with a stack pane as the root node and add it to the scene
        StackPane stackPane = new StackPane();
        Scene scene = new Scene(stackPane);
        stage.setScene(scene);

        // create a MapView to display the map and add it to the stack pane
        createLoggerRoute(stage);
        mapView = new MapView();
        crearComponents();
        stackPane.getChildren().addAll(mapView, btnRouteLogger);
        
        setupMap();
        setupAuthentication();
        setupGraphicsOverlay();
        addPointGraphic();
        addPolylineGraphic();
        addPolygonGraphic();
    }
    /**
     * Stops and releases all resources used in application.
     */
    @Override
    public void stop() {
        if (mapView != null) {
            mapView.dispose();
        }
    }
}
