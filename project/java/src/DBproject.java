/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Doctor");
				System.out.println("2. Add Patient");
				System.out.println("3. Add Appointment");
				System.out.println("4. Make an Appointment");
				System.out.println("5. List appointments of a given doctor");
				System.out.println("6. List all available appointments of a given department");
				System.out.println("7. List total number of different types of appointments per doctor in descending order");
				System.out.println("8. Find total number of patients per doctor with a given status");
				System.out.println("9. < EXIT");
				
				switch (readChoice()){
					case 1: AddDoctor(esql); break;
					case 2: AddPatient(esql); break;
					case 3: AddAppointment(esql); break;
					case 4: MakeAppointment(esql); break;
					case 5: ListAppointmentsOfDoctor(esql); break;
					case 6: ListAvailableAppointmentsOfDepartment(esql); break;
					case 7: ListStatusNumberOfAppointmentsPerDoctor(esql); break;
					case 8: FindPatientsCountWithStatus(esql); break;
					case 9: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	public static void AddDoctor(DBproject esql) {//1
		try{
			System.out.print("Doctor's ID:\n  ");
			String ID = in.readLine();
			System.out.print("Doctor's Name:\n  ");
			String Name = in.readLine();
			System.out.print("Doctor's specialty:\n  ");
			String Specialty = in.readLine();
			System.out.print("Doctor's Department:\n  ");
			String did = in.readLine();
        		String query = "INSERT INTO Doctor "+
           				"VALUES ("+ID+", '"+Name+"', '"+Specialty+"', "+did+")";

           		System.out.println(query);
			int rowCount = esql.executeQueryAndPrintResult(query);
           		System.out.println ("total row(s): " + rowCount);
      		}catch(Exception e){
         		System.err.println (e.getMessage());
      		}
	}

	public static void AddPatient(DBproject esql) {//2
		try{
			System.out.print("Patient's ID:\n  ");
			String patient_ID = in.readLine();
			System.out.print("Patient's Name:\n  ");
			String Name = in.readLine();
			System.out.print("Patient's Gender:\n  ");
			String gtype = in.readLine();
			System.out.print("Patient's Age:\n  ");
			String age = in.readLine();
			System.out.print("Patient's address:\n  ");
			String Address = in.readLine();
			System.out.print("Patient's number_of_appts:\n  ");
			String number_of_appts = in.readLine();
        		String query = "INSERT INTO Patient "+
           				"VALUES ("+patient_ID+", '"+Name+"', '"+gtype+"', "+age+", '"+Address+"', "+number_of_appts+")";

           		System.out.println(query);
			int rowCount = esql.executeQueryAndPrintResult(query);
           		System.out.println ("total row(s): " + rowCount);
      		}catch(Exception e){
         		System.err.println (e.getMessage());
      		}
	}

	public static void AddAppointment(DBproject esql) {//3
		try{
			System.out.print("Appointment's adate:\n  ");
			String adate = in.readLine();
			System.out.print("Appointment's time_slot:\n  ");
			String time_slot = in.readLine();
			System.out.print("Appointment's status:\n  ");
			String status = in.readLine();
			String query2 = "SELECT Appointment.appnt_ID FROM Appointment order by Appointment.appnt_ID desc limit 1;";
			String New_Aid = ((esql.executeQueryAndReturnResult(query2)).get(0)).get(0)+1;
        		String query = "INSERT INTO Appointment "+
           				"VALUES ("+New_Aid+", '"+adate+"', '"+time_slot+"', '"+status+"')";

			System.out.println(query);
           		int rowCount = esql.executeQueryAndPrintResult(query);
           		System.out.println ("total row(s): " + rowCount);
      		}catch(Exception e){
         		System.err.println (e.getMessage());
      		}		
	}


	public static void MakeAppointment(DBproject esql) {//4
		// Given a patient, a doctor and an appointment of the doctor that s/he wants to take, add an appointment to the DB
		try{
			System.out.print("Docter's ID:\n  ");
			String DID = in.readLine();
			System.out.print("Patient ID:\n  ");
			String PID = in.readLine();
			System.out.print("Appointment's id:\n  ");
			String AID = in.readLine();
        		String query = "SELECT * "+
           				"FROM Appointment "+
					"WHERE Appointment.appnt_ID="+AID+";";
					//"AND has_appointment.appt_id=Appointment.appnt_ID AND has_appointment.doctor_ID="+DID;

			System.out.print(query+"\n  ");
			System.out.println(esql.executeQueryAndReturnResult(query));
			//String date = ((esql.executeQueryAndReturnResult(query)).get(0)).get(1);
			//String time_slot = ((esql.executeQueryAndReturnResult(query)).get(0)).get(2);
           		String status = ((esql.executeQueryAndReturnResult(query)).get(0)).get(3);
			//System.out.println(status);
			//String query2 = "SELECT Appointment.appnt_ID FROM Appointment order by Appointment.appnt_ID desc limit 1;";
			//String New_Aid = ((esql.executeQueryAndReturnResult(query2)).get(0)).get(0);
			String query2 = "";
           		switch (status){
				case "AV":
				case "av":
					query2 = "UPDATE Appointment SET status='AC' WHERE appnt_ID="+AID+";";
					System.out.print(query2+"\n  ");
					esql.executeQueryAndPrintResult(query2);
					query2 = "INSERT has_appointment VALUES ("+AID+", "+DID+")";
					System.out.print(query2+"\n  ");
					esql.executeQueryAndPrintResult(query2);
					query2 = "UPDATE Patient SET Patient.number_of_appts=Patient.number_of_appts+1 WHERE Patient.patient_ID="+PID;
					System.out.print(query2+"\n  ");
					esql.executeQueryAndPrintResult(query2);
					break;
				case "AC":
				case "ac":
					query2 = "UPDATE Appointment SET status='WL' WHERE appnt_ID="+AID+";";
					System.out.print(query2+"\n  ");
					esql.executeQueryAndPrintResult(query2);
					query2 = "INSERT has_appointment VALUES ("+AID+", "+DID+")";
					System.out.print(query2+"\n  ");
					esql.executeQueryAndPrintResult(query2);
					query2 = "UPDATE Patient SET Patient.number_of_appts=Patient.number_of_appts+1 WHERE Patient.patient_ID="+PID;
					System.out.print(query2+"\n  ");
					esql.executeQueryAndPrintResult(query2);
					break;
				case "WL":
				case "wl":
					System.out.println("There already exist one/more waitlist for this appointment, do you still want to add a waitlist in this appointment? (Y/N)");
					String Type_In = in.readLine();
					if (Type_In=="Y") {
						query2 = "INSERT has_appointment VALUES ("+AID+", "+DID+")";
						System.out.print(query2+"\n  ");
						esql.executeQueryAndPrintResult(query2);
						query2 = "UPDATE Patient SET Patient.number_of_appts=Patient.number_of_appts+1 WHERE Patient.patient_ID="+PID;
						System.out.print(query2+"\n  ");
						esql.executeQueryAndPrintResult(query2);
					} else {
						if (Type_In=="N") {
							System.out.println("---Return to menu---");
						} else {
							System.out.println("Error type, Return to menu");
						}
					}
					break;
				case "PA":
				case "pa":
					System.out.println("ERROR: This is a past appointment");
					break;
			}
      		}catch(Exception e){
         		System.err.println (e.getMessage());
      		}
	}

	public static void ListAppointmentsOfDoctor(DBproject esql) {//5
		// For a doctor ID and a date range, find the list of active and available appointments of the doctor
		try{
			System.out.print("Docter's ID:\n  ");
			String ID = in.readLine();
			System.out.print("Appointment's Range(Earliest):\n  ");
			String Range1 = in.readLine();
			System.out.print("Appointment's Range(Latest):\n  ");
			String Range2 = in.readLine();
        		String query = "SELECT Appointment.appnt_ID, Appointment.adate, Appointment.time_slot, Appointment.status "+
           				"FROM Appointment, has_appointment "+
					"WHERE has_appointment.doctor_id="+ID+" AND has_appointment.appt_id=Appointment.appnt_ID "+
					"AND Appointment.status IN ('AC','AV') AND '"+
					Range1+"'<Appointment.adate AND Appointment.adate<'"+Range2+"'";

			System.out.println(query+"\n");
           		int rowCount = esql.executeQueryAndPrintResult(query);
           		System.out.println ("total row(s): " + rowCount);
      		}catch(Exception e){
         		System.err.println (e.getMessage());
      		}
	}

	public static void ListAvailableAppointmentsOfDepartment(DBproject esql) {//6
		// For a department name and a specific date, find the list of available appointments of the department
		try{
                        System.out.print("Department name:\n  ");
                        String Departname = in.readLine();
                        System.out.print("Specific date:\n  ");
                        String adate = in.readLine();
                        String query = "SELECT Appointment.appnt_ID, Appointment.adate, Appointment.time_slot, Appointment.status "+
                                        "FROM Appointment, has_appointment, Doctor, Department "+
                                        "WHERE has_appointment.doctor_id = Doctor.doctor_id AND "+
                                        "has_appointment.appt_id = Appointment.appnt_ID AND "+
                                        "Department.dept_ID = Doctor.did AND "+
                                        "Appointment.status = 'AV' AND "+
                                        "Appointment.adate = '"+adate+"' AND "+
                                        "Department.name='"+Departname+"'" ;

                        System.out.println(query+"\n");
                        int rowCount = esql.executeQueryAndPrintResult(query);
                        System.out.println ("total row(s): " + rowCount);
                }catch(Exception e){
                        System.err.println (e.getMessage());
                }
	}

	public static void ListStatusNumberOfAppointmentsPerDoctor(DBproject esql) {//7
		// Count number of different types of appointments per doctors and list them in descending order
		try{
                        String query =  "SELECT Doctor.name, Appointment.status , COUNT(*) AS NUMBER "+
                                        "FROM Appointment, has_appointment, Doctor "+
                                        "WHERE has_appointment.doctor_id = Doctor.doctor_id AND "+
                                        "has_appointment.appt_id = Appointment.appnt_ID "+
                                        "GROUP BY Doctor.name, Appointment.status "+
                                        "ORDER BY NUMBER Desc";

                        System.out.println(query+"\n");
                        int rowCount = esql.executeQueryAndPrintResult(query);
                        System.out.println ("total row(s): " + rowCount);
                }catch(Exception e){
                        System.err.println (e.getMessage());
                }
	}

	
	public static void FindPatientsCountWithStatus(DBproject esql) {//8
		// Find how many patients per doctor there are with a given status (i.e. PA, AC, AV, WL) and list that nuber per doctor.
		try{
			System.out.print("Status:\n  ");
                        String Status = in.readLine();
                        String query =  "SELECT Doctor.name, Appointment.status , COUNT(*) AS NumOfPatient "+
                                        "FROM Appointment, searches, Doctor, has_appointment "+
                                        "WHERE has_appointment.doctor_id = Doctor.doctor_id AND "+
					"has_appointment.appt_id = Appointment.appnt_ID AND "+
                                        "searches.aid = Appointment.appnt_ID AND "+
					"Appointment.status = '"+Status+"'"+
                                        "GROUP BY Doctor.name, Appointment.status "+
                                        "ORDER BY NumOfPatient Desc";

                        System.out.println(query+"\n");
                        int rowCount = esql.executeQueryAndPrintResult(query);
                        System.out.println ("total row(s): " + rowCount);
                }catch(Exception e){
                        System.err.println (e.getMessage());
                }

	}
}
