package mongo;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDB {
	String uri = "mongodb://localhost:27017";

	MongoClientURI clientURI = new MongoClientURI(uri);
	MongoClient mongoClient = new MongoClient(clientURI);
	
	MongoDatabase mongoDatabase = mongoClient.getDatabase("EasyCopy");
	MongoCollection<Document> values = mongoDatabase.getCollection("SavedValues");
	
	//Checks if there is any stored values in the Mongo collection.
	public boolean hasValues() {
		long count = values.count();	
		
		if (count > 0) {
			return true;
		}
		return false;
	}
	
	//Returns the user name saved in the data base.
	public String getUsername() {
		Document doc = (Document) values.find(new Document("savedValues", "saved")).first();
		return doc.getString("username");
	}
	
	//Returns the host name found in the data base.
	public String getHost() {
		Document doc = (Document) values.find(new Document("savedValues", "saved")).first();

		return doc.getString("host");
	}
	
	//Returns the port number saved in the data base.
	public String getPort() {
		Document doc = (Document) values.find(new Document("savedValues", "saved")).first();

		return doc.getString("port");
	}
	
	//Saves the user name in the data base.
	public void setUsername(String username) {
		Document doc = (Document) values.find(new Document("savedValues", "saved")).first();

		if (username != doc.get("username")) {	//Makes sure that we do not override the current saved value with the same value.
			Bson updatedUsername = new Document("username", username);
			Bson update = new Document("$set", updatedUsername);
			values.updateOne(doc, update);
		}
	}
	
	//Saves the host name in the data base.
	public void setHost(String host) {
		Document doc = (Document) values.find(new Document("savedValues", "saved")).first();

		if (host != doc.get("host")) {	//Makes sure that we do not override the current saved value with the same value.
			Bson updatedHost = new Document("host", host);
			Bson update = new Document("$set", updatedHost);
			values.updateOne(doc, update);
		}
	}
	
	//Saves the port number in the data base.
	public void setPort(String port) {
		Document doc = (Document) values.find(new Document("savedValues", "saved")).first();

		if (port != doc.get("port")) {	//Makes sure that we do not override the current saved value with the same value.
			Bson updatedPort = new Document("port", port);
			Bson update = new Document("$set", updatedPort);
			values.updateOne(doc, update);
		}
	}
	
	//Creates a new document to save information on.
	public void createDoc() {
		Document doc = new Document("savedValues", "saved");
		doc.append("username", "");	//Will save user name
		doc.append("host", "");	//Will save host name
		doc.append("port", "");	//Will save port number
		
		values.insertOne(doc);
	}
}
