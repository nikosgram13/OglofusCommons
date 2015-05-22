package me.nikosgram.oglofus.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class OglofusUtils
{
    public static < T > T notNull( T object )
    {
        return notNull( object, "The validated object is null" );
    }

    public static < T > T notNull( T object, String message )
    {
        if ( object == null )
        {
            throw new IllegalArgumentException( message );
        }
        return object;
    }

    public static < T > List< T > toPage( int page, int size, List< T > list )
    {
        List< T > returned = new ArrayList< T >();
        if ( ( size * page ) > list.size() )
        {
            return returned;
        }
        int end = ( size * page ) + size;
        if ( ( size * page ) + size > list.size() )
        {
            end = list.size();
        }
        for ( int i = ( size * page ); i < end; i++ )
        {
            returned.add( list.get( i ) );
        }
        return returned;
    }

    public static String notEmpty( String string )
    {
        return notEmpty( string, "The validated string is empty" );
    }

    public static String notEmpty( String string, String message )
    {
        if ( string == null || string.length() == 0 )
        {
            throw new IllegalArgumentException( message );
        }
        return string;
    }

    public static boolean equalClass( Class< ? > aClass, Class< ? > bClass )
    {
        return aClass == bClass || aClass.isAssignableFrom( bClass );
    }

    public static < T > T newInstance( Class< T > tClass, Object... initargs )
    {
        try
        {
            return tClass.getConstructor().newInstance( initargs );
        } catch ( InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static String capitalizeMessage( String message )
    {
        return message.substring( 0, 1 ) + message.substring( 1 ).toLowerCase();
    }
}
