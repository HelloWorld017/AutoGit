package com.He.W.onebone.Circuit.Cu.parser;
	
	import java.io.BufferedReader;
	import java.util.Locale;
	import java.util.Scanner;
	import java.util.TreeMap;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;

	import com.He.W.onebone.Circuit.Cu.exception.ParseException;

public class CCSParser {
	
		
	public static TreeMap<String, TreeMap<String, String>> parseCCS(BufferedReader br) throws ParseException{
		try{
			br.readLine();
			String filecontent = "";
			String buffer = "";
			while((buffer = br.readLine()) != null){
				filecontent += (buffer + "\n");
			}
			TreeMap<String, TreeMap<String, String>> contents = new TreeMap<String, TreeMap<String, String>>();
			TreeMap<String, String> attributes = new TreeMap<String, String>();
			
			Pattern startPattern = Pattern.compile("\\[([^/]+)\\]"); //[something] supports any unicodes
			Matcher startMatcher = startPattern.matcher(filecontent);
			
			Pattern endPattern = Pattern.compile("\\[/\\]"); //[/]
			Matcher endMatcher = endPattern.matcher(filecontent);
			
			while(startMatcher.find()){
				int start = startMatcher.start();
				int end = startMatcher.end();
					
				String tmp = filecontent.substring(start, end);
				String tag = tmp.substring(1, tmp.length() - 1);
	
				endMatcher.find();
				int tagBlockEnd = endMatcher.start();
				String tagBlock = filecontent.substring(end, tagBlockEnd);
				//Line terminator has 3 type = \r\n, \n, \r (CRLF, LF, CR). So I used Scanner.
				Scanner stringScanner = new Scanner(tagBlock);
				while(stringScanner.hasNextLine()){
					String s = stringScanner.nextLine();
					if(s.equals(""))continue;
					String[] data = s.split("=");
					attributes.put(data[0], data[1]);
				}
				
				stringScanner.close();
				contents.put(tag.toLowerCase(Locale.ENGLISH), attributes);
				attributes = new TreeMap<String, String>();
			}
			
			return contents;
		
		}catch(IllegalStateException e){
			throw new ParseException(ParseException.WRONG_FILE);
		}catch(StringIndexOutOfBoundsException e){
			throw new ParseException(ParseException.WRONG_FILE);
		}catch(ArrayIndexOutOfBoundsException e){
			throw new ParseException(ParseException.WRONG_FILE);
		}catch(NumberFormatException e){
			throw new ParseException(ParseException.WRONG_FILE);
		}catch(Exception e){
			throw new ParseException(ParseException.UNKNOWN);
		}
	}
}
