package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import com.smartcampus.resource.DiscoveryResource;
import com.smartcampus.resource.SensorRoomResource;
import com.smartcampus.resource.SensorResource;
import com.smartcampus.provider.GenericExceptionMapper;
import com.smartcampus.provider.LinkedResourceNotFoundMapper;
import com.smartcampus.provider.RoomNotEmptyMapper;
import com.smartcampus.provider.SensorUnavailableMapper;
import com.smartcampus.provider.LoggingFilter;


public class SmartCampusApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

      
        classes.add(DiscoveryResource.class);
        classes.add(SensorRoomResource.class);
        classes.add(SensorResource.class);

       
        classes.add(GenericExceptionMapper.class);
        classes.add(LinkedResourceNotFoundMapper.class);
        classes.add(RoomNotEmptyMapper.class);
        classes.add(SensorUnavailableMapper.class);
        classes.add(LoggingFilter.class);

        return classes;
    }
}
