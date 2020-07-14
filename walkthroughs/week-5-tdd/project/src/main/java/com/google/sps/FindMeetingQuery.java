// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> mandatory = new ArrayList<>();
    List<TimeRange> optional = new ArrayList<>();
    boolean noAttendees = (request.getAttendees().isEmpty() 
                            && request.getOptionalAttendees().isEmpty());

    if(request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return mandatory;
    }
    if(events.isEmpty() || noAttendees) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    ArrayList<Event> orderedEvents = new ArrayList<Event>(events);
    Collections.sort(orderedEvents, Event.ORDER_BY_START);

    int lastEventEndMandatory = 0;
    int lastEventEndOptional = 0;
    for(Event currEvent: orderedEvents) {
      Collection<String> attendees = currEvent.getAttendees();
      
      if(Collections.disjoint(attendees, request.getAttendees())) { // If required don't need to be there.
        if(Collections.disjoint(attendees, request.getOptionalAttendees())) { // And optional don't need to be there.
          continue;
        } else { // but optional need to be there.
          lastEventEndOptional = checkCurrEvent(currEvent, lastEventEndOptional, request, optional);
        }
      } else {
        lastEventEndMandatory = checkCurrEvent(currEvent, lastEventEndMandatory, request, mandatory);
        lastEventEndOptional = checkCurrEvent(currEvent, lastEventEndOptional, request, optional);
      }
    }

    // Add final period of the day to both lists.
    int timeAtEndOfDay = TimeRange.WHOLE_DAY.end() - lastEventEndMandatory;
    if(timeAtEndOfDay >= request.getDuration()) {
      TimeRange option = TimeRange.fromStartDuration(lastEventEndMandatory, timeAtEndOfDay);
      mandatory.add(option);
    }
    
    timeAtEndOfDay = TimeRange.WHOLE_DAY.end() - lastEventEndOptional;
    if(timeAtEndOfDay >= request.getDuration()) {
      TimeRange option = TimeRange.fromStartDuration(lastEventEndOptional, timeAtEndOfDay);
      optional.add(option);
    }
    
    // If we have events where everyone can attend, or no required attendees,
    // return optional, otherwise, return only those times when mandatory attendees can attend.
    return (!optional.isEmpty() || request.getAttendees().isEmpty()) ? optional : mandatory;
  }

  private int checkCurrEvent(Event currEvent, int lastEventEnd, 
    MeetingRequest request, List<TimeRange> options) {
    TimeRange currRange = currEvent.getWhen();
    int timeBetweenEvents = currRange.start() - lastEventEnd;

    // Overlapping events will fail this check without explicitly checking.
    if(timeBetweenEvents >= request.getDuration()) {
      TimeRange option = TimeRange.fromStartDuration(lastEventEnd, timeBetweenEvents);
      options.add(option);
    }
    if(currRange.end() > lastEventEnd) {
      lastEventEnd = currRange.end();
    }
    return lastEventEnd;
  }
}
