package ueditor.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Matcher
import java.util.regex.Pattern

class PathFormat {
	
	private static final String TIME = "time"
    private static final String FULL_YEAR = "yyyy"
    private static final String YEAR = "yy"
    private static final String MONTH = "mm"
    private static final String DAY = "dd"
    private static final String HOUR = "hh"
    private static final String MINUTE = "ii"
    private static final String SECOND = "ss"
    private static final String RAND = "rand"

    private static Date currentDate = null

    static String parse (String input ) {
		
		Pattern pattern = Pattern.compile( "\\{([^\\}]+)\\}", Pattern.CASE_INSENSITIVE  )
        Matcher matcher = pattern.matcher(input)

        currentDate = new Date()

        StringBuffer sb = new StringBuffer()

        while ( matcher.find() ) {
			
			matcher.appendReplacement(sb, getString( matcher.group( 1 ) ) )

        }
		
		matcher.appendTail(sb)

        return sb.toString()
    }
	
	/**
	 * 格式化路径, 把windows路径替换成标准路径
	 * @param input 待格式化的路径
	 * @return 格式化后的路径
	 */
	static String format ( String input ) {
		
		return input.replace( "\\", "/" )

    }

    static String parse (String input,String filename ) {
	
		Pattern pattern = Pattern.compile( "\\{([^\\}]+)\\}", Pattern.CASE_INSENSITIVE  )
        Matcher matcher = pattern.matcher(input)
        String matchStr = null

        currentDate = new Date()

        StringBuffer sb = new StringBuffer()

        while ( matcher.find() ) {
			
			matchStr = matcher.group( 1 )
            if ( matchStr.indexOf( "filename" ) != -1 ) {
				filename = filename.replace( '$', '\\$' ).replaceAll( "[\\/:*?\"<>|]", "" )
                matcher.appendReplacement(sb, filename )
            } else {
				matcher.appendReplacement(sb, getString( matchStr ) )
            }
			
		}
		
		matcher.appendTail(sb)

        return sb.toString()
    }
		
	private static String getString ( String pattern ) {
		
		pattern = pattern.toLowerCase()

        // time 处理
		if ( pattern.indexOf( TIME ) != -1 ) {
			return getTimestamp()
        } else if ( pattern.indexOf( FULL_YEAR ) != -1 ) {
			return getFullYear()
        } else if ( pattern.indexOf( YEAR ) != -1 ) {
			return getYear()
        } else if ( pattern.indexOf( MONTH ) != -1 ) {
			return getMonth()
        } else if ( pattern.indexOf( DAY ) != -1 ) {
			return getDay()
        } else if ( pattern.indexOf( HOUR ) != -1 ) {
			return getHour()
        } else if ( pattern.indexOf( MINUTE ) != -1 ) {
			return getMinute()
        } else if ( pattern.indexOf( SECOND ) != -1 ) {
			return getSecond()
        } else if ( pattern.indexOf( RAND ) != -1 ) {
			return getRandom( pattern )
        }
		
		return pattern

    }

	private static String getTimestamp () {
		return System.currentTimeMillis() + ""
    }
	
	private static String getFullYear () {
		return new SimpleDateFormat( "yyyy" ).format( currentDate )
    }
	
	private static String getYear () {
		return new SimpleDateFormat( "yy" ).format( currentDate )
    }
	
	private static String getMonth () {
		return new SimpleDateFormat( "MM" ).format( currentDate )
    }
	
	private static String getDay () {
		return new SimpleDateFormat( "dd" ).format( currentDate )
    }
	
	private static String getHour () {
		return new SimpleDateFormat( "HH" ).format( currentDate )
    }
	
	private static String getMinute () {
		return new SimpleDateFormat( "mm" ).format( currentDate )
    }
	
	private static String getSecond () {
		return new SimpleDateFormat( "ss" ).format( currentDate )
    }
	
	private static String getRandom ( String pattern ) {
		
		int length = 0
        pattern = pattern.split( ":" )[ 1 ].trim()

        length = Integer.parseInt( pattern )

        return ( Math.random() + "" ).replace( ".", "" ).substring( 0, length )

    }

    static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
