

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class MovieRentApp {
	

	public static void main(String[] args) throws IOException {
		
		
		String filename = "Movie_Data.csv";
		//gets the array for the movies
		File f = new File(filename);
		String[] header = null;
		String[][] body = null;
		
		try {
			Scanner s = new Scanner(f);
			header = getHeader(s);
			body = getBody(s);
			
		} catch (Exception e) {
			System.out.println("Error - " + e.getMessage());
		}

		
		boolean[] rented = null; // UNAVAILABLE MOVIES / RENTED MOVIES BY OTHER PEOPLE
		String[] person = null; // list of people
		File file = new File("movieRented.txt");
		File filePerson = new File ("moviePerson.txt");
		try {
			Scanner s1 = new Scanner(file);
			Scanner s2 = new Scanner(filePerson);
			
			rented = getRented(s1,file);	
			
			person = getPerson(s2,filePerson);	//gets the list of the names of person who borrowed movies
			
		}catch (Exception e) {
			System.out.println("Error - " + e.getMessage());
		}
		
	
		boolean[] currentRented = new boolean[body.length];// the movies rented by the CURRENT person
		
		
		Scanner userInput = new Scanner(System.in);
		int input = 0;
		
		System.out.println("Enter your name (make sure there are no unnecessary spaces): ");
		String customer = userInput.nextLine();
		
		
		do {
		System.out.println("======================================================================");
		System.out.println("===========================!Rent Movies!==============================");
		System.out.println("======================================================================");
		System.out.println("1. Developer");
		System.out.println("2. Search");
		System.out.println("3. Rent");
		System.out.println("4. Return");
		System.out.println("5. Earnings");
		System.out.println("6. Exit ");
		System.out.println("Pick [1-6]:");
		
		int temp = userInput.nextInt();
		input = temp;
		
		switch (input) {
		case 1 : developer();
				break;
		case 2 : search(header, body, rented,currentRented);
				break;
		case 3 : rent(body,currentRented,rented);
				break;
		case 4 : returnMovies(body,rented,currentRented,customer, person);
				break;
		case 5 : displayEarnings(header,body);
				break;
		}
		
		}	while(input != 6);	
		
		receipt(body,currentRented,customer);
		updateMovieCount(currentRented);
		
		//UPDATE THE RENTEDMOVIES AND PERSON NAME DATA
		
		updatePerson(customer,person,currentRented);
		updateRented(rented,currentRented,person);
		
	}
	
	
	
	
	
	
	
	public static void developer() {
		System.out.println("Developer : Nicholas Januar (100347148) - CPSC 1150-003");
	}
	
	public static void search(String[] header , String[][] body,boolean[] rent, boolean[] currentRented) {
		
		int input = 0;
		Scanner userInput2 = new Scanner(System.in);
		do {
		System.out.println("==============================SEARCH==================================");
		System.out.println("1. All available movies");
		System.out.println("2. By genre(e.g. Adventure, Horror, etc...)");
		System.out.println("3. By title");
		System.out.println("4. Exit search");
		System.out.println("Pick [1-4]: ");
		input = userInput2.nextInt();
		
		switch(input) {
		case 1 : displayMovies(header,body,rent,currentRented);
				break;
		case 2 : genreSearch(body,rent,currentRented);
				break;
		case 3 : titleSearch(body,rent,currentRented);
				break;
		}
		
		}while(input != 4);
			
	}
	
	
	public static void displayMovies(String[] header, String[][] body, boolean[] rent, boolean[] currentRented) {
		System.out.printf("%s %-30s%-45s%10s\n", "id",header[0],header[1],header[2]);
		
		for (int y=0; y < body.length; y++) {
			if(!rent[y] && !currentRented[y])
				System.out.printf("%2d %-30s%-45s%10.2f\n", y,body[y][0],body[y][1],Double.parseDouble(body[y][2].substring(1)));
		}
		
	}
	
	public static void genreSearch(String[][] body, boolean[] rent, boolean[] currentRented) {
		
		Scanner input2 = new Scanner(System.in);
		
		System.out.println("Search by genre: ");
		String g = input2.nextLine();
		String movieIndex = findMovieGenre (g,body,rent,currentRented);
		displayMoviesGenre(movieIndex,body);
		
	}
	
	private static String findMovieGenre(String genre, final String [][]movies, boolean[] rent, boolean[] currentRented) {
		String movieIndex = "";
		String []typeOfMovie;
		boolean found;
		
		for (int x=0; x < movies.length; x++) { 
			typeOfMovie = movies[x][0].split("\\|"); 
			found = false; 
			for (int i=0; i < typeOfMovie.length && !found; i++) {
				if (typeOfMovie[i].equalsIgnoreCase(genre) && rent[i] == false && currentRented[i] == false) {
					found = true;
					movieIndex += x + ",";
				}
			}
		}
		return movieIndex;
	}
	
	private static void displayMoviesGenre(String movieIndex, final String [][]m) {
		String [] sIndex = movieIndex.split(",");
		int index; 
		
		for (int x = 0 ; x < sIndex.length; x++) {
			index = Integer.parseInt(sIndex[x]); 
			
			System.out.printf("movie id:%2s | title:%-40s %10s\n", sIndex[x], m[index][1],m[index][2]);
		}
		
	}
	
	public static void titleSearch(String[][] body, boolean[] rent, boolean[] currentRented) {
		Scanner userInput = new Scanner(System.in);
		System.out.println("Search by a word (ex: the / life / etc.) : ");
		String search = userInput.nextLine();
		String[] words = null;
		String temp = null;
		
		for(int x = 0 ; x < body.length; x++) {
			temp = body[x][1].replace(',', ' ');
			words = temp.split(" ");
			
			for(int y = 0 ; y < words.length ; y++) {
				if(search.equalsIgnoreCase(words[y]) && rent[y] == false && currentRented[y] == false)
					System.out.printf("movie id:%2s | title:%-40s %10s\n", x, body[x][1],body[x][2]);
				
			}
		}
		
	}
	
	
	private static void rent(String[][] body, boolean[] currentRented, boolean[] rented) {
		int id;
		Scanner userInput = new Scanner(System.in);
		
		do {
		System.out.print("Which movie do you want to rent (type the movie id, -1 to exit) ? ");
		id = userInput.nextInt();

		if ((id >= 0 && id < body.length) && (!currentRented[id] )&& (!rented[id])) {
			currentRented[id] = true ; 
			System.out.println("The movie " + "\"" + body[id][1] + "\"" + " is added to cart.");
		}
		}while(id != -1);
	}
	
	private static void returnMovies(final String[][]m, boolean []rent, boolean[] currentRented, String customer,String[] person) {

			Scanner s = new Scanner(System.in);
			int id;
			do {
	
				for (int x=0 ; x < rent.length; x++) {
					if ((rent[x] && customer.equalsIgnoreCase(person[x]) || currentRented[x])) 
						System.out.printf("movie id:%2d | title:%s\n", x, m[x][1]);
				}
				System.out.print("Which movie do you want to return(type the movie id, -1 to exit) ? ");
				id = s.nextInt();
			
				if ((id >= 0 && id < rent.length) && rent[id] && customer.equalsIgnoreCase(person[id]))
					rent[id] = false ; 
				else if ((id >= 0 && id < rent.length) && currentRented[id])
					currentRented[id] = false;
			} while (id != -1);
		}

	public static void receipt(final String[][]body, final boolean []rent, String customer) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy - HH:mm");
		Date d = new Date();
		String s = formatter.format(d);

		double fee;
		double sum = 0 ;
		System.out.println("=============================================================");
		System.out.println("Customer: " + customer);
		System.out.println(s);
		System.out.println();
		System.out.printf("%-40s%10s\n", "Movie title", "Rent Fee");
		for (int x = 0 ; x < rent.length; x++) {
			if (rent[x]) { //
				fee = Double.parseDouble(body[x][2].substring(1));
				sum += fee;
				System.out.printf("%-40s%10.2f\n",body[x][1],fee);
				
			}
		}
		System.out.println();
		System.out.printf("%-40s%10.2f\n","Total",sum);
		//=====================================
		//writing the receipt file
		File f = new File("transactionsData.txt");
		sum = 0;
		try {
			FileWriter fw = new FileWriter(f,true);
			fw.append(String.format("======\n"));
			fw.append(String.format("Customer: " + customer +"\n"));
			fw.append(String.format(s));
			fw.append(String.format("\n"));
			//0,1,2,3,4
			int count = 0;
			for(int x = 0 ; x < rent.length; x ++) {
				if(rent[x]) {
					if (count == 0)
						fw.append(String.format(x +""));
					else
						fw.append(String.format("," + x));
					count++;
				}	
			}
			fw.append(String.format("\n"));
			fw.append(String.format("%-40s%10s\n", "Movie title", "Rent Fee"));
			for (int x = 0 ; x < rent.length; x++) {
				if (rent[x]) { //
					fee = Double.parseDouble(body[x][2].substring(1));
					sum += fee;
					fw.append(String.format("%-40s%10.2f\n",body[x][1],fee));
				}
			}
			fw.append(String.format("\n"));
			fw.append(String.format("%-40s%10.2f\n","Total",sum));
			fw.close();
		}catch (Exception e) {
			System.out.println("Error - " + e.getMessage());
		}
		f = null;
	}
	
	public static boolean[] getRented(Scanner s1,File f) {
		boolean[] rented = new boolean[20] ;
		String line;
		String[] each;
		
		if(!s1.hasNext()) {	// if theres no data yet, make default data 
			try {
				FileWriter fw = new FileWriter(f,true);
				fw.append(String.format("false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false"));
				fw.close();
			} catch (Exception e) {
				System.out.println("Error - " + e.getMessage());
			}			
		}
		while(s1.hasNext()) {
			line = s1.nextLine()	;
			each = line.split(",");
			for (int x = 0 ; x < each.length ; x++) {
				if (each[x].equalsIgnoreCase("false"))
					rented[x] = false;
				else 
					rented[x] = true;
			}
		}
		return rented;
	}
	
	
	
	private static void updateRented(boolean[] rented, boolean[] currentRented, String[] person) throws IOException {
		
		File f = new File("movieRented.txt");
		Scanner sc = new Scanner(f);
		StringBuffer buffer = new StringBuffer();
		while(sc.hasNextLine()) {
			buffer.append(sc.nextLine());
		}
		String fileContents = buffer.toString();
		sc.close();
		
		String oldString = "";
		String newString = "";

		System.out.println();
		for (int x = 0 ; x < 20 ; x++) {
			if(x!= 19)
				oldString += rented[x] +",";
			else if (x==19)
				oldString += rented[x];

		}

		
		
		for(int x = 0 ; x < 20 ; x++) {
			if (currentRented[x] == true && rented[x] == false)
				rented[x] = true;
			if (currentRented[x] == false && rented[x] == false)
				rented[x] = false;
		}
		for (int x = 0 ; x < 20 ; x++) {
			if (person[x].equalsIgnoreCase("AVAILABLE")){
				rented[x] = false;
			}
			
		}
		
		 
				for (int x = 0 ; x < 20 ; x++) {
			if(x!= 19)
				newString += rented[x] +",";
			else if (x==19)
				newString += rented[x];
			
			
		}
				
		
//		System.out.println(oldString);
//		System.out.println(newString);
		
		fileContents = fileContents.replaceAll(oldString, newString);
		FileWriter fw = new FileWriter("movieRented.txt");
		fw.append(fileContents);
		fw.flush();
		

}
	
	
	public static String[] getPerson(Scanner s ,File f ) {
		String[] person = new String[20];
		String line ;
		if(!s.hasNext()) {	// if theres no person data yet, make default data
			try {
				FileWriter fw = new FileWriter(f,true);
				fw.append(String.format("AVAILABLE,AVAILABLE,AVAILABLE,AVAILABLE,AVAILABLE,AVAILABLE,AVAILABLE,AVAILABLE,AVAILABLE"
						+ ",AVAILABLE,AVAILABLE,AVAILABLE,AVAILABLE,AVAILABLE,AVAILABLE,AVAILABLE,AVAILABLE,AVAILABLE,AVAILABLE,AVAILABLE"));
				fw.close();
				
			} catch (Exception e) {
				System.out.println("Error - " + e.getMessage());
			}
		}
		
		while(s.hasNext()) {
			line = s.nextLine()	;
			person = line.split(",");
		}
		
		return person;
	}
	
	
	
	
	public static void updatePerson(String customer, String[] person, boolean[] currentRented) throws IOException {
		
		
		File f = new File("moviePerson.txt");
		Scanner sc = new Scanner(f);
		StringBuffer buffer = new StringBuffer();
		while(sc.hasNextLine()) {
			buffer.append(sc.nextLine());
		}
		String fileContents = buffer.toString();
		sc.close();

		String oldString = "";
		String newString = "";

		System.out.println();
		for (int x = 0 ; x < 20 ; x++) {
			if(x!= 19)
				oldString += person[x] +","	;
			else if (x==19)
				oldString += person[x];

		}

		for(int x = 0 ; x < 20 ; x++) {
			if(currentRented[x] == true) 
				person[x] = customer;
			if (customer.equalsIgnoreCase(person[x]) && currentRented[x] == false)
				person[x] = "AVAILABLE";
			
		}
				for (int x = 0 ; x < 20 ; x++) {
			if(x!= 19)
				newString += person[x] +",";
			else if (x==19)
				newString += person[x];

		}
				
				
//		System.out.println(oldString);
//		System.out.println(newString);
		
		fileContents = fileContents.replaceAll(oldString, newString);
		FileWriter fw = new FileWriter("moviePerson.txt");
		fw.append(fileContents);
		fw.flush();
		
	}
	
	
	public static int[] getMovieCount() {
		int[] movieCount = new int[20];
		
		//////////////////////GETTING THE DATA////////////////////////
		File f = new File("movieRentedCount.txt");
		try {
			Scanner s = new Scanner(f);
			FileWriter fw = new FileWriter(f,true);
			String line = ""; 
			String[] temp = null ;
			if (!s.hasNextLine()) {// if theres no line yet , make a default line with all values = 0
				fw.append(String.format("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0"));
			}
			while(s.hasNextLine()) {
				line = s.nextLine();
				temp = line.split(",");
			}
			for (int x = 0 ; x < temp.length ; x++) {
				movieCount[x] = Integer.parseInt(temp[x]);
			}
			
		} catch (Exception e) {
			System.out.println("Error - " + e.getMessage());
		}
		
		
		
		return movieCount;
	}
	
	public static void updateMovieCount(boolean[] currentRented) throws IOException {
		
		File f = new File("movieRentedCount.txt");
		Scanner sc = new Scanner(f);
		StringBuffer buffer = new StringBuffer();
		while(sc.hasNextLine()) {
			buffer.append(sc.nextLine());
		}
		String fileContents = buffer.toString();
		sc.close();

		String oldString = "";
		String newString = "";

		int[] movieCount = getMovieCount();
		
		
		System.out.println();
		for (int x = 0 ; x < 20 ; x++) {
			if(x!= 19)
				oldString += movieCount[x] +","	;
			else if (x==19)
				oldString += movieCount[x];

		}

		for(int x = 0 ; x < 20 ; x++) {
			if(currentRented[x] == true) 
				movieCount[x] += 1;	// ADDS 1 COUNT IF THE MOVIE X WAS RENTED		
		}
				for (int x = 0 ; x < 20 ; x++) {
			if(x!= 19)
				newString += movieCount[x] +",";
			else if (x==19)
				newString += movieCount[x];

		}
				
//		System.out.println(oldString);
//		System.out.println(newString);
		
		fileContents = fileContents.replaceAll(oldString, newString);
		FileWriter fw = new FileWriter("movieRentedCount.txt");			//updates the movieCount file
		fw.append(fileContents);
		fw.flush();
		
	}
	
	public static void displayEarnings(String[] header, String[][] body) {
		int[] movieCount = getMovieCount();
		//									MOVIE TIT, RENT FEE, # BORROWED, EARNINGS
		System.out.printf("%-30s%-20s%-15s%-10s\n","Movie Title","Rent Fee","# of times borrowed", "Earnings");

		double sum = 0;
		for (int x = 0 ; x < 20 ; x++) {
			if(movieCount[x] > 0 ) {
				double temp = Double.parseDouble(body[x][2].substring(1));
				System.out.printf("%-30s %-20s %-15d %-10.2f \n",body[x][1],body[x][2],movieCount[x], ((double)temp * movieCount[x] ));
				sum += (double)Double.parseDouble(body[x][2].substring(1))*movieCount[x];
			}
		}
		System.out.printf("%-30s%-20s%-15s%8.2f \n","","  ","Total",sum);
	
		
	}
	
	
	
	private static String[][] getBody(Scanner s) {
		String[][] body = null;
		String line;
		int row = 1;
		while(s.hasNext()) {
			line = s.nextLine();
			body = addData(body,row, line);
			row++;
			
		}
		
		return body;
	}

	private static String[][] addData(String [][]original, int size, String data) {
		String[][] temp ;
		
		temp = new String[size][];

		for (int y=0 ; y < size-1; y++) 
		{
			temp[y] = original[y];
		}

		if (data.indexOf("\"") > 0) {

			temp[size-1] = data.split("\"");
			temp[size-1][0] = temp[size-1][0].split(",")[0]; 
			temp[size-1][2] = temp[size-1][2].substring(1); 
		} else {
			temp[size-1] = data.split(",");
		}
		return temp;
	}

	private static String[] getHeader(Scanner s) {
		String []head = null;
		String line;
		if (s.hasNext()) {
			line = s.nextLine();
			head = line.split(",");
		}
		return head;	
	}
	

}
