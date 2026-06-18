package com.example.timetracker.timeentry.mapper;

import com.example.timetracker.timeentry.dto.ActiveTimerResponse;
import com.example.timetracker.timeentry.dto.TimeEntryResponse;
import com.example.timetracker.timeentry.entity.TimeEntry;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TimeEntryMapper {

    ActiveTimerResponse toActiveTimerResponse(TimeEntry timeEntry);

    TimeEntryResponse toTimeEntryResponse(TimeEntry timeEntry);

}
