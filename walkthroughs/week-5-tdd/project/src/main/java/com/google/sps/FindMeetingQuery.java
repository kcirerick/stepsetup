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
  /** Main function for FindMeetingQuery.
    * Takes a colletion of event and a meeting request and returns times when that meeting
    * can reasonably occur. */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> mandatory = new ArrayList<>();
    List<TimeRange> optional = new ArrayList<>();

    boolean noAttendees = (request.getAttendees().isEmpty() 
      && request.getOptionalAttendees().isEmpty());

    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return mandatory; // Currently empty.
    }
    if (events.isEmpty() || noAttendees) {

      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    ArrayList<Event> orderedEvents = new ArrayList<Event>(events);
    Collections.sort(orderedEvents, Event.ORDER_BY_START);

    // Populate mandatory and optional.
    int[] lastEventEnds = partialQuery(orderedEvents, request, mandatory, optional);
    completeQuery(lastEventEnds[0], lastEventEnds[1], request, mandatory, optional);
    
    // If we have events where everyone can attend, or no required attendees,
    // return optional, otherwise, return only those times when mandatory attendees can attend.
    return (!optional.isEmpty() || request.getAttendees().isEmpty()) ? optional : mandatory;
  }

  /** Populates the optional and mandatory lists with TimeRanges that work for each
    * based on the list of events. 
    * Returns the end of both optional and mandatory events. */
  private int[] partialQuery(ArrayList<Event> events, MeetingRequest request, 
    List<TimeRange> mandatory, List<TimeRange> optional) {
    int[] lastEventEnds = {0,0};
    for(Event currEvent: events) {
      Collection<String> attendees = currEvent.getAttendees();
      int lastEventEndMandatory = lastEventEnds[0];
      int lastEventEndOptional = lastEventEnds[1];
      lastEventEnds = updateListsWithCurrEvent(lastEventEndMandatory, lastEventEndOptional, request, optional,
        mandatory, attendees, currEvent);
    }
    return lastEventEnds;
  }

  /** Populates the optional and mandatory lists with TimeRanges that work after the end of all events. 
    * Returns nothing. */
  private void completeQuery(int lastEventEndMandatory, int lastEventEndOptional, MeetingRequest request,
    List<TimeRange> mandatory, List<TimeRange> optional) {
    // Add final period of the day to both lists.
    int timeAtEndOfDay = TimeRange.WHOLE_DAY.end() - lastEventEndMandatory;
    if (timeAtEndOfDay >= request.getDuration()) {
      TimeRange option = TimeRange.fromStartDuration(lastEventEndMandatory, timeAtEndOfDay);
      mandatory.add(option);
    }
    
    timeAtEndOfDay = TimeRange.WHOLE_DAY.end() - lastEventEndOptional;
    if (timeAtEndOfDay >= request.getDuration()) {
      TimeRange option = TimeRange.fromStartDuration(lastEventEndOptional, timeAtEndOfDay);
      optional.add(option);
    }
  }

  /** Checks attendee availability for currEvent and adds a TimeRange based on availability.
    * Returns the end times of current or previous meeting depending on availability. */
  private int[] updateListsWithCurrEvent(int lastEventEndMandatory, int lastEventEndOptional, MeetingRequest request,
    List<TimeRange> optional, List<TimeRange> mandatory, Collection<String> attendees, Event currEvent) {

    if (Collections.disjoint(attendees, request.getAttendees())) { // If required don't need to be there.
      if (!Collections.disjoint(attendees, request.getOptionalAttendees())) { // But optional do.
        lastEventEndOptional = checkCurrEvent(currEvent, lastEventEndOptional, request, optional);
      }
    } else {
      lastEventEndMandatory = checkCurrEvent(currEvent, lastEventEndMandatory, request, mandatory);
      lastEventEndOptional = checkCurrEvent(currEvent, lastEventEndOptional, request, optional);
    }
    int[] lastEventEnds = {lastEventEndMandatory, lastEventEndOptional};
    return lastEventEnds;
  }

  /** Checks whether a the time between the current and last event is large enough to host 
    * the requested meeting and updates options accordingly. */
  private int checkCurrEvent(Event currEvent, int lastEventEnd, 
    MeetingRequest request, List<TimeRange> options) {
    TimeRange currRange = currEvent.getWhen();
    int timeBetweenEvents = currRange.start() - lastEventEnd;

    // Overlapping events will fail this check without explicitly checking.
    if (timeBetweenEvents >= request.getDuration()) {
      TimeRange option = TimeRange.fromStartDuration(lastEventEnd, timeBetweenEvents);
      options.add(option);
    }
    if (currRange.end() > lastEventEnd) {
      lastEventEnd = currRange.end();
    }
    return lastEventEnd;
  }
}
