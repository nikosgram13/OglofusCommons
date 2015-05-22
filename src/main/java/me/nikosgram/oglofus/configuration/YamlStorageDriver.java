/*
 * Copyright 2014-2015 Nikos Grammatikos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://raw.githubusercontent.com/nikosgram13/OglofusProtection/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.nikosgram.oglofus.configuration;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class YamlStorageDriver< T > implements StorageDriver< T >
{
    protected final ConfigurationDriver< T > driver;
    protected final Yaml                     YAML;
    protected final Path                     path;
    protected long modified = 0L;

    protected YamlStorageDriver( ConfigurationDriver< T > driver )
    {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultScalarStyle( DumperOptions.ScalarStyle.PLAIN );
        dumperOptions.setDefaultFlowStyle( DumperOptions.FlowStyle.FLOW );
        dumperOptions.setPrettyFlow( true );
        dumperOptions.setWidth( 9999999 );
        Representer representer = new Representer();
        representer.addClassTag( driver.configuration, Tag.MAP );
        YAML = new Yaml( representer, dumperOptions );
        this.driver = driver;
        path = Paths.get(
                driver.workDirectory.toString() +
                        "/" +
                        driver.name +
                        "." +
                        ConfigurationType.Yaml.extension
        );
    }

    public boolean create()
    {
        Path parent = path.getParent();
        if ( !Files.exists( parent ) ) try
        {
            Files.createDirectories( parent );
        } catch ( IOException e )
        {
            e.printStackTrace();
        }
        if ( !Files.exists( path ) ) try
        {
            Files.createFile( path );
        } catch ( IOException e )
        {
            e.printStackTrace();
        }
        return Files.exists( path );
    }

    public void save()
    {
        if ( !create() ) return;
        if ( driver.model == null ) return;
        try ( OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream( path.toFile() ),
                Charset.forName( "UTF-8" )
        ) )
        {
            YAML.dump( driver.model, writer );
        } catch ( IOException e )
        {
            e.printStackTrace();
        }
        try
        {
            modified = Files.getLastModifiedTime( path ).toMillis();
        } catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void load()
    {
        if ( !create() ) return;
        try
        {
            if ( Files.getLastModifiedTime( path ).toMillis() == modified ) return;
        } catch ( IOException e )
        {
            e.printStackTrace();
        }
        try ( InputStreamReader reader = new InputStreamReader(
                new FileInputStream( path.toFile() ), Charset.forName( "UTF-8" )
        ) )
        {
            driver.model = YAML.loadAs( reader, driver.configuration );
        } catch ( IOException e )
        {
            e.printStackTrace();
        }
        if ( driver.model == null ) try
        {
            driver.model = driver.configuration.newInstance();
            save();
        } catch ( InstantiationException | IllegalAccessException e )
        {
            e.printStackTrace();
        }
    }
}