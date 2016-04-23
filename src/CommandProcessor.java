import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * This class process all the commands
 */
public class CommandProcessor {

	Import imp;
	World world; 
	String par1;
	String par2;
	String par3;
	
	RandomAccessFile log;
	DataBase dataBase;
	
	int count;
	
	GISrecord GISrecord;
	Point point;
	
	NameIndex nameIndex;
	CoordinateIndex coordIndex;
	BufferPool buffPool;
	
	WhatIsIn whatIsIn;
	WhatIsAt whatIsAt;
	WhatIsInC whatIsInC;
	WhatIsInL whatIsInL;
	WhatIs whatIs;
	
	DateFormat dateFormat = new SimpleDateFormat("E M d HH:mm:ss ");
	DateFormat dateFormat2 = new SimpleDateFormat(" yyyy");
	Date date = new Date();

	/**
	 * Constructor: initializes everything needed to process all the commands including
	 * the log file, database, hash table, and prQuadTree
	 */
	public CommandProcessor(String par1, String par2, String par3) throws IOException {
		
		this.par1 = par1;
		this.par2 = par2;
		this.par3 = par3;

		log = new RandomAccessFile(par3, "rw");
		dataBase = new DataBase(par1);
		count = 1;

		coordIndex = new CoordinateIndex();
		buffPool = new BufferPool();
		nameIndex = new NameIndex ();
	}

	/**
	 * Processes a command
	 * @param command
	 */
	@SuppressWarnings("unused")
	public void process(String command) throws IOException {
		//if the command string contains a semicolon, print to log
		if (command.contains(";")) {
			log.writeBytes(command + "\n");
		}
		else {
			//checks if the command is world
			if (command.contains("world")) {
				world = new World(command);
				log.writeBytes(command);
				log.writeBytes("\n\nGIS Program\n\ndbFile:     " + par1 + 
						"\nscript:     " + par2 + "\nlog:        " + par3 + 
						"\nStart time: " + dateFormat.format(date) + "EDT" + dateFormat2.format(date) + 
						"\n" + "Quadtree children are printed in the order SW  SE  NE  NW\n" + 
						"--------------------------------------------------------------------------------\n");

				//create a new CoordinateIndex object/tree
				coordIndex.createTree(world.SW, world.SE, world.NE, world.NW);
				log.writeBytes("\nLatitude/longitude values in index entries are shown as signed integers, " + 
						"in total seconds.\n\n" + "World boundaries are set to:\n" + 
						"              " + world.NW + "\n   " + world.SW + "                " + 
						world.SE + "\n              " + world.NE + 
						"\n--------------------------------------------------------------------------------" + "\n");
			}
			//checks if the command is import
			else if (command.contains("import")) {
				
				//creates a new import object
				imp = new Import (command);
				//find the length of the file we are importing
				int length2 = imp.length() - 1;
				int imported = length2;

				while (length2 != 0) {
					//reads from the import file, parse the line, and return the coordinates
					long[] array = imp.getCoordinates();

					//if the coordinates are in bounds
					if (world.isInBounds(array[1], array[0])) {

						//write the current line to the database
						dataBase.write(imp.getCurrentImportLine());	

						//pass the current import line to the GISrecord and create a new object
						GISrecord = new GISrecord(imp.getCurrentImportLine(), dataBase.getOffset());

						//create a new point object with the latitude and longitude
						point = new Point(GISrecord.getLong(), GISrecord.getLati(), dataBase.getOffset());

						//insert point into the tree
						coordIndex.insert(point);

						//insert a GISrecord into hash table 
						nameIndex.addObj(GISrecord);

					}else {

					}
					length2--;
				}

				log.writeBytes("Command " + count + ":  " + command);
				log.writeBytes("\n\nImported Features by name: " + imported);
				log.writeBytes("\nLongest probe sequence:    " + nameIndex.getMaxProbe());
				log.writeBytes("\nImported Locations:        " + imported);
				log.writeBytes("\n--------------------------------------------------------------------------------\n");
				count++;
			}
			//if we are debugging the quadTree
			else if (command.contains("debug") && command.contains("quad")) {
				log.writeBytes("Command " + count + ":  " + command + "\n\n");
				count++;
				coordIndex.debugQuadTree(log);
				log.writeBytes("\n--------------------------------------------------------------------------------\n");
			}
			//if we are debugging the hash table
			else if (command.contains("debug") && command.contains("hash")) {
				log.writeBytes("Command " + count + ":  " + command + "\n\n");
				count++;
				log.writeBytes("Format of display is\n");
				log.writeBytes("Slot number: data record\n");
				log.writeBytes("Current table size is " + nameIndex.hash.getLength() + "\n");
				log.writeBytes("Number of elements in table is " + nameIndex.hash.getSize() + "\n\n");
				log.writeBytes(nameIndex.toString());
				log.writeBytes("--------------------------------------------------------------------------------\n");
			}
			//if we are debugging the pool
			else if (command.contains("debug") && command.contains("pool")) {
				log.writeBytes("Command " + count + ":  " + command + "\n\n");
				count++;
				log.writeBytes(buffPool.toString());
				log.writeBytes("--------------------------------------------------------------------------------\n");
			}
			//checks if the command is what is in -l
			else if (command.contains("what_is_in") && command.contains("-l")) {
				
				//creates a new what is in l object
				whatIsInL = new WhatIsInL (command);

				log.writeBytes("Command " + count + ":  " + command + "\n\n");
				count++;

				//create a vector to store all the records found in the region
				Vector<Point> v = coordIndex.findInRegion(whatIsInL.getLong(), whatIsInL.getLati(), 
						whatIsInL.getHalfY(), whatIsInL.getHalfX());

				//adds up all the size of the offset list of each point object in the vector
				int sizeSum = 0;
				for (int i = 0; i < v.size(); i++) {
					sizeSum += v.get(i).getSize();
				}

				log.writeBytes("   The following " + sizeSum + " features were found in (" + whatIsInL.getLongStr()
				+ " +/- " + whatIsInL.getHalfY() + ", " + whatIsInL.getLatiStr() 
				+ " +/- " + whatIsInL.getHalfX() + ")\n");

				//for every record in the vector we want to check if it is in the buffer pool
				for (int i = 0; i < v.size(); i++) {
					for (int j = 0; j < v.get(i).getOffsetList().size(); j++) {
						//finds the record in the buffer pool
						GISrecord buffRecord = buffPool.findRecord(v.get(i).getOffsetAt(j));
						if (buffRecord != null) {
							log.writeBytes("  Feature ID   : " + buffRecord.getFeatureID() + "\n");
							log.writeBytes("  Feature Name : " + buffRecord.getfeatName() + "\n");
							log.writeBytes("  Feature Cat  : " + buffRecord.getFeatureCat() + "\n");
							log.writeBytes("  State        : " + buffRecord.getStateAbbr() + "\n");
							log.writeBytes("  County       : " + buffRecord.getCounty() + "\n");
							log.writeBytes("  Latitude     : " + buffRecord.getLatiS() + "\n");
							log.writeBytes("  Longitude    : " + buffRecord.getLongS() + "\n");
							if (!buffRecord.getSrcLong().equals("")) {
								log.writeBytes("  Src Long     : " + buffRecord.getSrcLong() + "\n");
							}
							if (!buffRecord.getSrcLati().equals("")) {
								log.writeBytes("  Src Lat      : " + buffRecord.getSrcLati() + "\n");
							}
							log.writeBytes("  Elev in ft   : " + buffRecord.getElev() + "\n");
							log.writeBytes("  USGS Quad    : " + buffRecord.getCounty() + "\n");
							log.writeBytes("  Date created : " + buffRecord.getDateCreated() + "\n");
							if (buffRecord.getDateMod() != null) {
								log.writeBytes("  Date mod     : " + buffRecord.getDateMod() + "\n");
							}
							log.writeBytes("\n");
						}
						//if the record is not in the buffer pool, we want to grab from the database
						else {

							long off = v.get(i).getOffsetAt(j);
							//creates a GIS record and add it to the buffer pool
							GISrecord record = new GISrecord(dataBase.getRecord(off), off);
							buffPool.addRecord(record);

							log.writeBytes("  Feature ID   : " + dataBase.getFeatureID(off) + "\n");
							log.writeBytes("  Feature Name : " + dataBase.getFeatName(off) + "\n");
							log.writeBytes("  Feature Cat  : " + dataBase.getFeatureCat(off) + "\n");
							log.writeBytes("  State        : " + dataBase.getState(off) + "\n");
							log.writeBytes("  County       : " + dataBase.getCounty(off) + "\n");
							log.writeBytes("  Latitude     : " + dataBase.getLat(off) + "\n");
							log.writeBytes("  Longitude    : " + dataBase.getLong(off) + "\n");
							if (!dataBase.getSrcLong(off).equals("")) {
								log.writeBytes("  Src Long     : " + dataBase.getSrcLong(off) + "\n");
							}
							if (!dataBase.getSrcLati(off).equals("")) {
								log.writeBytes("  Src Lat      : " + dataBase.getSrcLati(off) + "\n");
							}
							log.writeBytes("  Elev in ft   : " + dataBase.getElev(off) + "\n");
							log.writeBytes("  USGS Quad    : " + dataBase.getCounty(off) + "\n");
							log.writeBytes("  Date created : " + dataBase.getDateCreated(off) + "\n");
							try {
								log.writeBytes("  Date mod     : " + dataBase.getDateMod(off) + "\n");
							}
							catch(ArrayIndexOutOfBoundsException e) {

							}
							log.writeBytes("\n");
						}
					}
				}
				log.writeBytes("--------------------------------------------------------------------------------\n");
			}
			else if (command.contains("what_is_in") && command.contains("-c")) {

				//creates a what is in -c object
				whatIsInC = new WhatIsInC (command);

				//creates a vector to store all the records found in the region
				Vector<Point> v = coordIndex.findInRegion(whatIsInC.getLong(), whatIsInC.getLati(), 
						whatIsInC.getHalfY(), whatIsInC.getHalfX());

				log.writeBytes("Command " + count + ":  " + command + "\n\n");
				count++;

				//adds up all the size of the offset list of each point object in the vector
				int sizeSum = 0;
				for (int i = 0; i < v.size(); i++) {
					sizeSum += v.get(i).getSize();
				}

				log.writeBytes(sizeSum + " features were found in (" + whatIsInC.getLongStr()
				+ " +/- " + whatIsInC.getHalfY() + ", " + whatIsInC.getLatiStr() 
				+ " +/- " + whatIsInC.getHalfX() + ")");
				log.writeBytes("\n--------------------------------------------------------------------------------\n");
			}
			else if (command.contains("what_is_in")) {

				//creates a new what is in object
				whatIsIn = new WhatIsIn (command);
				log.writeBytes("Command " + count + ":  " + command + "\n\n");
				count++;

				//creates a vector that holds all the records that lie within the bounds
				Vector<Point> v = coordIndex.findInRegion(whatIsIn.getLong(), whatIsIn.getLati(), 
						whatIsIn.getHalfY(), whatIsIn.getHalfX());

				//if the vector is empty
				if (v.isEmpty()) {
					log.writeBytes("   Nothing was found in (" + whatIsIn.getLongStr()
					+ " +/- " + whatIsIn.getHalfY() + ", " + whatIsIn.getLatiStr() 
					+ " +/- " + whatIsIn.getHalfX() + ")\n");
				}
				else {

					//adds up all the size of the offset list of each point object in the vector
					int sizeSum = 0;
					for (int i = 0; i < v.size(); i++) {
						sizeSum += v.get(i).getSize();
					}

					log.writeBytes("   The following " + sizeSum + " features were found in (" + 
					whatIsIn.getLongStr() + " +/- " + whatIsIn.getHalfY() + 
					", " + whatIsIn.getLatiStr() + " +/- " + whatIsIn.getHalfX() + ")\n");

					for (int i = 0; i < v.size(); i++) {
						//go through the offset list
						for (int j = 0; j < v.get(i).getOffsetList().size(); j++) {
							//use offset to find record in the buffer pool
							//if the record exists, print it out to log
							GISrecord buffRecord = buffPool.findRecord(v.get(i).getOffsetAt(j));
							if (buffRecord != null) {
								log.writeBytes(v.get(i).getOffsetAt(j) + ":  " + 
								buffRecord.getfeatName() + "  " + buffRecord.getStateAbbr() + 
								"  " + buffRecord.getLatiS() + "  " + buffRecord.getLongS() + "\n");
							}
							//else we want to grab the record from the database and add to the buffer pool
							else {
								long off = v.get(i).getOffsetAt(j);
								GISrecord record = new GISrecord(dataBase.getRecord(off), off);
								buffPool.addRecord(record);
								String dataBaseRecord = dataBase.getRecord(v.get(i).getOffsetAt(j));
								log.writeBytes(off + ":  " + 
										dataBase.getFeatName(off) + "  " + 
										dataBase.getState(off) + "  " + 
										dataBase.getLat(off) + "  " + 
										dataBase.getLong(off) + "\n");
							}
						}
					}
				}

				log.writeBytes("--------------------------------------------------------------------------------\n");
			}
			else if (command.contains("what_is_at")) {
				log.writeBytes("Command " + count + ":  " + command + "\n\n");
				count++;
				
				//creates a what is at object
				whatIsAt = new WhatIsAt(command);

				try{
					//create a new point object with the longitude and latitude 
					Point p = new Point(whatIsAt.getLong(), whatIsAt.getLati());

					//use the newly created point object to search in the tree
					Point result = coordIndex.find(p);
					//create a vector to to store the offset list of the point object
					Vector<Long> offset = coordIndex.find(p).getOffsetList();

					log.writeBytes("   The following features were found at " + 
							whatIsAt.getLongStr() + "   " + whatIsAt.getLatiStr() + ":\n");
					
					for (int i = 0; i < result.getOffsetList().size(); i++) {
						long off = result.getOffsetAt(i);
						log.writeBytes(off + ":  " + dataBase.getFeatName(off) + "  " + 
						dataBase.getCounty(off) + "  " + dataBase.getState(off) + "\n");
					}
				}
				//if nothing is found 
				catch(NullPointerException e) {
					log.writeBytes("   Nothing was found at " + whatIsAt.getLongStr() + "   " + whatIsAt.getLatiStr() + "\n");
				}
				log.writeBytes("--------------------------------------------------------------------------------\n");
			}
			else if (command.contains("what_is")) {

				//creates a what is object
				whatIs = new WhatIs(command);

				log.writeBytes("Command " + count + ": " + command + "\n\n");
				count++;

				try {
			
					//create a new GIS record and search in the hash table
					GISrecord gisRecord = new GISrecord(command);
					GISrecord result = nameIndex.findNoOffset(gisRecord);
					
					for ( int i = 0; i < result.sizeOfList() ; i++) {
						//search for the record in the buffer pool
						GISrecord buffRecord = buffPool.findRecord(result.getOffsetAt(i));
						if (buffRecord != null) {
							log.writeBytes(result.getOffsetAt(i) + ":  " + buffRecord.getCounty() + 
									"  " + buffRecord.getLongS() + "  " + buffRecord.getLatiS() + "\n");
						}
						//if record is not in the buffer pool, search in the database
						else {
							long off = result.getOffsetAt(i);
							GISrecord record = new GISrecord(dataBase.getRecord(off), off);
							if (record != null) {
								buffPool.addRecord(record);
								log.writeBytes(result.getOffsetAt(i) + ":  " + 
								dataBase.getCounty(result.getOffsetAt(i)) + "  " + 
								dataBase.getLong(result.getOffsetAt(i)) + "  " + 
								dataBase.getLat(result.getOffsetAt(i)) + "\n");
							}
						}
					}
				}
				//if no records match
				catch(NullPointerException e) {
					log.writeBytes("No records match " + whatIs.getFeatName() + " and " + whatIs.getState() + "\n");
				}
				log.writeBytes("--------------------------------------------------------------------------------\n");
			}
			//reached the quit command
			else {	
				log.writeBytes("Command " + count + ":  quit\n\n");
				log.writeBytes("Terminating execution of commands.\n");
				log.writeBytes("End time: " + dateFormat.format(date) + "EDT" + dateFormat2.format(date));
				log.writeBytes("\n--------------------------------------------------------------------------------\n");
			}
		}
	}
}

