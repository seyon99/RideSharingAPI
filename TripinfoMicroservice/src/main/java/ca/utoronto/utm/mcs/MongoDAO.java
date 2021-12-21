package ca.utoronto.utm.mcs;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MongoDAO {

    private MongoCollection<Document> collection;
    private final String username = "root";
    private final String password = "123456";
    private final String dbName = "trip";
    private final String collectionName = "trips";

    public MongoDAO() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("MONGODB_ADDR");
        String uriParam = addr + "://%s:%s@mongodb:27017"; // is bolt required?
        String uriDb = String.format(uriParam, this.username, this.password);
        MongoClient mongoClient = MongoClients.create(uriDb);
        MongoDatabase database = mongoClient.getDatabase(this.dbName);
        this.collection = database.getCollection(this.collectionName);
    }


    public ObjectId confirm(String driver, String passenger, long startTime) {
        Document doc = new Document();
        doc.put("driver", driver);
        doc.put("passenger", passenger);
        doc.put("startTime", startTime);

        this.collection.insertOne(doc);
        ObjectId id = doc.getObjectId("_id");

        // REF: https://stackoverflow.com/questions/3338999/get-id-of-last-inserted-document-in-a-mongodb-w-java-driver
        return id;
    }

    public boolean patchTrip(String id, int distance, long endTime, String timeElapsed, int discount, double totalCost,
                             double driverPayout) {
        BasicDBObject updateFields = new BasicDBObject();
        updateFields.append("distance", distance);
        updateFields.append("endTime", endTime);
        updateFields.append("timeElapsed", timeElapsed);
        updateFields.append("discount", discount);
        updateFields.append("totalCost", totalCost);
        updateFields.append("driverPayout", driverPayout);

        UpdateResult res = this.collection.updateOne(new BasicDBObject("_id", new ObjectId(id)),
                new BasicDBObject("$set", updateFields));

        return res.getModifiedCount() > 0; // expression on left of equality is 1 if update successful
    }

    public JSONObject getPassengerTrips(String uid) {
        JSONObject trips = new JSONObject();
        JSONArray tripsArray = new JSONArray();
        try {
            FindIterable<Document> tripDocs = this.collection.find(Filters.eq("passenger",
                    uid));

            for (Document tripDoc : tripDocs) {
                JSONObject trip = new JSONObject();
                String _id = tripDoc.get("_id").toString();
                int distance = tripDoc.getInteger("distance");
                double totalCost = tripDoc.getDouble("totalCost");
                int discount = tripDoc.getInteger("discount");
                long startTime = tripDoc.getLong("startTime");
                long endTime = tripDoc.getLong("endTime");
                String timeElapsed = tripDoc.getString("timeElapsed");
                String driver = tripDoc.getString("driver");
                // populate json object prior to appending to json array
                trip.put("_id", _id);
                trip.put("distance", distance);
                trip.put("totalCost", totalCost);
                trip.put("discount", discount);
                trip.put("startTime", startTime);
                trip.put("endTime", endTime);
                trip.put("timeElapsed", timeElapsed);
                trip.put("driver", driver);
                tripsArray.put(trip);
            }

            trips.put("trips", tripsArray);

        } catch (Exception e) {
            return null;
        }
        return trips;
    }

    public JSONObject getDriverTrips(String uid) {
        JSONObject trips = new JSONObject();
        JSONArray tripsArray = new JSONArray();
        try {
            FindIterable<Document> tripDocs = this.collection.find(Filters.eq("driver",
                    uid));

            for (Document tripDoc : tripDocs) {
                JSONObject trip = new JSONObject();
                String _id = tripDoc.get("_id").toString();
                int distance = tripDoc.getInteger("distance");
                double driverPayout = tripDoc.getDouble("driverPayout");
                long startTime = tripDoc.getLong("startTime");
                long endTime = tripDoc.getLong("endTime");
                String timeElapsed = tripDoc.getString("timeElapsed");
                String passenger = tripDoc.getString("passenger");
                // populate json object prior to appending to json array
                trip.put("_id", _id);
                trip.put("distance", distance);
                trip.put("driverPayout", driverPayout);
                trip.put("startTime", startTime);
                trip.put("endTime", endTime);
                trip.put("timeElapsed", timeElapsed);
                trip.put("passenger", passenger);
                tripsArray.put(trip);
            }

            trips.put("trips", tripsArray);

        } catch (Exception e) {
            return null;
        }
        return trips;
    }

    public JSONObject getDriverTime(String _id) {
        Document document = this.collection.find(Filters.eq("_id", new ObjectId("4f693d40e4b04cde19f17205"))).first();
        if (document == null) {
            return null;
        } else {
            String passenger = document.getString("passenger");
            String driver = document.getString("driver");
            JSONObject ret = new JSONObject();
            try {
                ret.put("driver", driver);
                ret.put("passenger", passenger);
                return ret;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}