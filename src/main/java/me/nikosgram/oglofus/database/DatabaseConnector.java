package me.nikosgram.oglofus.database;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.net.URLCodec;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class DatabaseConnector implements DatabaseDriver
{
    private static final StringEncoder encoder = new URLCodec();
    private static final StringDecoder decoder = new URLCodec();

    private DatabaseDriver driver = null;

    public DatabaseConnector( DatabaseDriver driver )
    {
        this.driver = driver;
    }

    public static String reformedListToString( List< String > list, Boolean encode )
    {
        List< String > StringList = new ArrayList< String >();
        if ( encode )
        {
            for ( String s : list )
            {
                StringList.add( encodeString( s ) );
            }

        } else
        {
            for ( String s : list )
            {
                StringList.add( s.trim() );
            }
        }
        String Returned = "StartAT*" + StringList.toString() + "*EndAT";
        return Returned.replaceFirst( "StartAT\\*\\[", "" ).replaceFirst( "\\]\\*EndAT", "" );
    }

    public static String reformedListToString( Collection< String > list, Boolean encode )
    {
        List< String > returned = new ArrayList< String >();
        Collections.addAll( returned, list.toArray( new String[ list.size() ] ) );
        return reformedListToString( returned, encode );
    }


    public static String reformedListToString( String[] list, Boolean encode )
    {
        List< String > returned = new ArrayList< String >();
        Collections.addAll( returned, list );
        return reformedListToString( returned, encode );
    }

    public static String reformedListToString( List< String > list )
    {
        return reformedListToString( list, true );
    }

    public static String reformedListToString( String[] list )
    {
        List< String > StringList = new ArrayList< String >();
        Collections.addAll( StringList, list );
        return reformedListToString( StringList, true );
    }

    public static String reformedListToString( Collection< String > list )
    {
        List< String > returned = new ArrayList< String >();
        Collections.addAll( returned, list.toArray( new String[ list.size() ] ) );
        return reformedListToString( returned, true );
    }

    public static String encodeString( String s )
    {
        s = s.trim();
        if ( ( s.startsWith( "'" ) ) && ( s.endsWith( "'" ) ) )
        {
            try
            {
                return "'" + encoder.encode( s ) + "'";
            } catch ( EncoderException e )
            {
                throw new RuntimeException( e );
            }

        } else
        {
            try
            {
                return "'" + encoder.encode( s ) + "'";
            } catch ( EncoderException e )
            {
                throw new RuntimeException( e );
            }
        }
    }

    public static String decodeString( String s )
    {
        s = s.trim();
        try
        {
            return decoder.decode( s );
        } catch ( DecoderException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static String reformMessage( String Message )
    {
        if ( !Message.endsWith( ";" ) )
        {
            Message = Message + ";";
        }
        return Message;
    }

    public Connection openConnection()
    {
        if ( driver == null )
        {
            return null;
        }
        driver.openConnection();
        if ( checkConnection() )
        {
            return driver.getConnection();
        }
        return null;
    }

    public Boolean checkConnection()
    {
        if ( driver == null )
        {
            return false;
        }
        return driver.checkConnection();
    }

    public void closeConnection()
    {
        if ( driver == null )
        {
            return;
        }
        if ( !checkConnection() )
        {
            return;
        }
        driver.closeConnection();
    }

    public Connection getConnection()
    {
        if ( driver == null )
        {
            return null;
        }
        return driver.getConnection();
    }

    public String name()
    {
        if ( driver == null )
        {
            return "";
        }
        return driver.name();
    }

    public void execute( String message )
    {
        if ( checkConnection() )
        {
            String reformedMessage = reformMessage( message );
            Statement statement = getStatement();
            try
            {
                statement.execute( reformedMessage );
            } catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
            closeStatement( statement );
        }
    }

    public Object get( String message, String wanted )
    {
        Object object = null;
        if ( checkConnection() )
        {
            String reformedMessage = reformMessage( message );
            ResultSet resultSet = getResultSet( reformedMessage );
            try
            {
                while ( resultSet.next() )
                {
                    object = resultSet.getObject( wanted );
                }
            } catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
            closeResultSet( resultSet );
        }
        return object;
    }

    public final String getString( String message, String wanted )
    {
        Object object = get( message, wanted );
        if ( ( object != null ) && ( ( object instanceof String ) ) )
        {
            return decodeString( ( String ) object );
        }
        return "";
    }

    public Integer getInteger( String message, String wanted )
    {
        Object object = get( message, wanted );
        if ( ( object != null ) && ( ( object instanceof Integer ) ) )
        {
            return ( Integer ) object;
        }
        return 0;
    }

    public Boolean getBoolean( String message, String wanted )
    {
        Object object = get( message, wanted );
        if ( ( object != null ) && ( ( object instanceof Boolean ) ) )
        {
            return ( Boolean ) object;
        }
        return null;
    }

    public Boolean exists( String message )
    {
        Boolean aBoolean = false;
        if ( checkConnection() )
        {
            String reformedMessage = reformMessage( message );
            ResultSet resultSet = getResultSet( reformedMessage );
            try
            {
                aBoolean = resultSet.next();
            } catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
            closeResultSet( resultSet );
        }
        return aBoolean;
    }

    public List< Object > getList( String message, String wanted )
    {
        List< Object > objects = new ArrayList< Object >();
        if ( checkConnection() )
        {
            String reformedMessage = reformMessage( message );
            ResultSet resultSet = getResultSet( reformedMessage );
            try
            {
                while ( resultSet.next() )
                {
                    objects.add( resultSet.getObject( wanted ) );
                }
            } catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
            closeResultSet( resultSet );
        }
        return objects;
    }

    public List< String > getStringList( String message, String wanted )
    {
        List< String > strings = new ArrayList< String >();
        for ( Object object : getList( message, wanted ) )
        {
            if ( ( object != null ) && ( ( object instanceof String ) ) )
            {
                strings.add( decodeString( ( String ) object ) );
            }
        }
        return strings;
    }

    public List< Integer > getIntegerList( String message, String wanted )
    {
        List< Integer > integers = new ArrayList< Integer >();
        for ( Object object : getList( message, wanted ) )
        {
            if ( ( object != null ) && ( ( object instanceof Integer ) ) )
            {
                integers.add( ( Integer ) object );
            }
        }
        return integers;
    }

    public Boolean existsInside( String message, String wanted, String type )
    {
        for ( String s : getStringList( message, wanted ) )
        {
            if ( s.equals( type ) )
            {
                return true;
            }
        }
        return false;
    }

    public Statement getStatement()
    {
        if ( checkConnection() )
        {
            try
            {
                return getConnection().createStatement();
            } catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
        }
        return null;
    }

    public ResultSet getResultSet( String message )
    {
        if ( checkConnection() )
        {
            Statement statement = getStatement();
            if ( statement == null )
            {
                return null;
            }
            try
            {
                return getStatement().executeQuery( message );
            } catch ( Exception e )
            {
                closeStatement( statement );
                throw new RuntimeException( e );
            }
        }
        return null;
    }

    public void closeStatement( Statement statement )
    {
        try
        {
            statement.close();
        } catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public void closeResultSet( ResultSet resultSet )
    {
        try
        {
            closeStatement( resultSet.getStatement() );
        } catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
        try
        {
            resultSet.close();
        } catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public Boolean exists( String table, String where, String like )
    {
        Boolean ReturnedBoolean = false;
        if ( checkConnection() )
        {
            String reformedMessage = null;
            reformedMessage = reformMessage(
                    "SELECT * FROM " +
                            table +
                            " WHERE " +
                            where +
                            " LIKE " +
                            encodeString( like )
            );
            ResultSet resultSet = getResultSet( reformedMessage );
            try
            {
                ReturnedBoolean = resultSet.next();
            } catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
            closeResultSet( resultSet );
        }
        return ReturnedBoolean;
    }

    public Object getObject( String table, String where, String like, String Wanted )
    {
        Object object = null;
        if ( checkConnection() )
        {
            String reformedMessage = null;
            reformedMessage = reformMessage(
                    "SELECT * FROM " +
                            table +
                            " WHERE " +
                            where +
                            " LIKE " +
                            encodeString( like )
            );
            ResultSet resultSet = getResultSet( reformedMessage );
            try
            {
                while ( resultSet.next() )
                {
                    object = resultSet.getObject( Wanted );
                }
            } catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
            closeResultSet( resultSet );
        }
        return object;
    }

    public String getString( String table, String where, String like, String Wanted )
    {
        Object object = getObject( table, where, like, Wanted );
        if ( object == null )
        {
            return "";
        }
        if ( ( object instanceof String ) )
        {
            return decodeString( ( String ) object );
        }
        return decodeString( object.toString() );
    }

    public Map< String, Object > getObjectMap( String table, String where, String like, String[] Wanted )
    {
        Map< String, Object > ReturnedMap = new HashMap< String, Object >();
        for ( String s : Wanted )
        {
            ReturnedMap.put( s, getObject( table, where, like, s ) );
        }
        return ReturnedMap;
    }

    public Map< String, String > getStringMap( String table, String where, String like, String[] Wanted )
    {
        Map< String, String > ReturnedMap = new HashMap< String, String >();
        for ( String s : Wanted )
        {
            ReturnedMap.put( s, getString( table, where, like, s ) );
        }
        return ReturnedMap;
    }

    public List< Object > getList( String table, String where, String like, String Wanted )
    {
        List< Object > objects = new ArrayList< Object >();
        if ( checkConnection() )
        {
            String reformedMessage = null;
            reformedMessage = reformMessage(
                    "SELECT * FROM " +
                            table +
                            " WHERE " +
                            where +
                            " LIKE " +
                            encodeString( like )
            );
            ResultSet resultSet = getResultSet( reformedMessage );
            try
            {
                while ( resultSet.next() )
                {
                    objects.add( resultSet.getObject( Wanted ) );
                }
            } catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
            closeResultSet( resultSet );
        }
        return objects;
    }

    public List< String > getStringList( String table, String where, String like, String Wanted )
    {
        List< String > strings = new ArrayList< String >();
        for ( Object object : getList( table, where, like, Wanted ) )
        {
            if ( object != null )
            {
                if ( ( object instanceof String ) )
                {
                    strings.add( decodeString( ( String ) object ) );
                } else strings.add( decodeString( object.toString() ) );
            }
        }
        return strings;
    }

    public Boolean existsInside( String table, String where, String like, String Wanted, String Type )
    {
        for ( String s : getStringList( table, where, like, Wanted ) )
        {
            if ( s.equals( Type ) )
            {
                return true;
            }
        }
        return false;
    }

    public void createTable( String table, String[] values )
    {
        execute(
                "CREATE TABLE IF NOT EXISTS " +
                        table +
                        " (" +
                        reformedListToString( values, false ) +
                        ")"
        );
    }

    public void insert( String table, Map< String, String > values )
    {
        execute(
                "INSERT INTO " +
                        table +
                        " (" +
                        reformedListToString( values.keySet(), false ) +
                        ") VALUES (" +
                        reformedListToString( values.values() ) +
                        ")"
        );
    }

    public void update( String table, String where, String like, String Change, String To )
    {
        execute(
                "UPDATE " +
                        table +
                        " SET " +
                        Change +
                        " = " +
                        encodeString( To ) +
                        " WHERE " +
                        where +
                        " LIKE " +
                        encodeString( like )
        );
    }

    public void update( String table, String where, String like, Map< String, String > values )
    {
        for ( String Change : values.keySet() )
        {
            update( table, where, like, Change, ( String ) values.get( Change ) );
        }
    }
}
