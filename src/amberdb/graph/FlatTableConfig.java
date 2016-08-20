package amberdb.graph;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class FlatTableConfig {
	
	private static Map<String, List<String>> tableColumns = new TreeMap<>();
	static {
		tableColumns.put("node", Arrays.asList("accessConditions", "alias", "commentsExternal", "commentsInternal", "expiryDate", "internalAccessConditions", "localSystemNumber", "notes", "recordSource", "restrictionType"));
		tableColumns.put("work", Arrays.asList("abstract", "access", "accessConditions", "acquisitionCategory", "acquisitionStatus", "additionalContributor", "additionalCreator", "additionalSeries", "additionalSeriesStatement", "additionalTitle", "addressee", "adminInfo", "advertising", "algorithm", "alias", "allowHighResdownload", "allowOnsiteAccess", "alternativeTitle", "altform", "arrangement", "australianContent", "bestCopy", "bibId", "bibLevel", "bibliography", "captions", "carrier", "category", "childRange", "classification", "collection", "collectionNumber", "commentsExternal", "commentsInternal", "commercialStatus", "condition", "constraint", "contributor", "coordinates", "copyingPublishing", "copyrightPolicy", "copyRole", "copyStatus", "copyType", "correspondenceHeader", "correspondenceId", "correspondenceIndex", "coverage", "creator", "creatorStatement", "currentVersion", "dateCreated", "dateRangeInAS", "dcmAltPi", "dcmCopyPid", "dcmDateTimeCreated", "dcmDateTimeUpdated", "dcmRecordCreator", "dcmRecordUpdater", "dcmSourceCopy", "dcmWorkPid", "depositType", "digitalStatus", "digitalStatusDate", "displayTitlePage", "eadUpdateReviewRequired", "edition", "encodingLevel", "endChild", "endDate", "eventNote", "exhibition", "expiryDate", "extent", "findingAidNote", "firstPart", "folder", "folderNumber", "folderType", "form", "genre", "heading", "holdingId", "holdingNumber", "html", "illustrated", "ilmsSentDateTime", "immutable", "ingestJobId", "interactiveIndexAvailable", "internalAccessConditions", "isMissingPage", "issn", "issueDate", "language", "localSystemNumber", "manipulation", "materialFromMultipleSources", "materialType", "metsId", "moreIlmsDetailsRequired", "notes", "occupation", "otherNumbers", "otherTitle", "preferredCitation", "preservicaId", "preservicaType", "printedPageNumber", "provenance", "publicationCategory", "publicationLevel", "publicNotes", "publisher", "rdsAcknowledgementReceiver", "rdsAcknowledgementType", "recordSource", "relatedMaterial", "repository", "restrictionsOnAccess", "restrictionType", "rights", "scaleEtc", "scopeContent", "segmentIndicator", "sendToIlms", "sensitiveMaterial", "sensitiveReason", "series", "sheetCreationDate", "sheetName", "standardId", "startChild", "startDate", "subHeadings", "subject", "subType", "subUnitNo", "subUnitType", "summary", "tempHolding", "tilePosition", "timedStatus", "title", "totalDuration", "uniformTitle", "vendorId", "versionNumber", "workCreatedDuringMigration", "workPid"));
		tableColumns.put("file", Arrays.asList("application", "applicationDateCreated", "bitDepth", "bitrate", "blobId", "blockAlign", "brand", "carrierCapacity", "channel", "checksum", "checksumGenerationDate", "checksumType", "codec", "colourProfile", "colourSpace", "compression", "cpLocation", "dateDigitised", "dcmCopyPid", "device", "deviceSerialNumber", "duration", "durationType", "encoding", "equalisation", "fileContainer", "fileFormat", "fileFormatVersion", "fileName", "fileSize", "framerate", "imageLength", "imageWidth", "location", "manufacturerMake", "manufacturerModelName", "manufacturerSerialNumber", "mimeType", "notes", "orientation", "photometric", "reelSize", "resolution", "resolutionUnit", "samplesPerPixel", "samplingRate", "software", "softwareSerialNumber", "soundField", "speed", "surface", "thickness", "toolId", "zoomLevel"));
		tableColumns.put("description", Arrays.asList("alternativeTitle", "city", "country", "digitalSourceType", "event", "exposureFNumber", "exposureMode", "exposureProgram", "exposureTime", "fileFormat", "fileSource", "focalLength", "gpsVersion", "isoCountryCode", "isoSpeedRating", "latitude", "latitudeRef", "lens", "longitude", "longitudeRef", "mapDatum", "meteringMode", "province", "subLocation", "timestamp", "whiteBalance", "worldRegion"));
		tableColumns.put("tag", Arrays.asList("name", "description"));
		tableColumns.put("party", Arrays.asList("name","orgUrl","suppressed","logoUrl"));
		tableColumns.put("flatedge", Arrays.asList("v_out","v_in","edge_order"));
		tableColumns.put("acknowledge", Arrays.asList("v_out","v_in","edge_order", "ackType", "date", "kindOfSupport", "weighting", "urlToOriginal"));
		tableColumns = Collections.unmodifiableMap(tableColumns); // Don't let anyone change this.
	}

	private static Map<String, String> columnToTableMap = new HashMap<>();
	static {
		for (Entry<String, List<String>> entry: tableColumns.entrySet()) {
			String table = entry.getKey();
			for (String column: entry.getValue()) {
				columnToTableMap.put(column, table);
			}
		}
		columnToTableMap = Collections.unmodifiableMap(columnToTableMap);
	}

	public static Map<String, List<String>> getTableColumns() {
		return tableColumns;
	}

	public static String getTableForColumn(String name) {
		return columnToTableMap.get(name);
	}

	
}
