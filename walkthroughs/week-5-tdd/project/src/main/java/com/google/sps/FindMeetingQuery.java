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
    List<TimeRange> options = new ArrayList<>();
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return options;
    }
    if (events.isEmpty() || request.getAttendees().isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    ArrayList<Event> orderedEvents = new ArrayList<Event>(events);
    Collections.sort(orderedEvents, Event.ORDER_BY_START);

    // Add all non-conflicting times before end of day.
    int lastEventEnd = 0;
    for(Event currEvent: orderedEvents) {
      // If none of the requested attendees need to be at currEvent, we can ignore it.
      if (Collections.disjoint(currEvent.getAttendees(), request.getAttendees())) continue;
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
    }

    // Add final period of the day if it all attendees have the time.
    int timeAtEndOfDay = TimeRange.WHOLE_DAY.end() - lastEventEnd;
    if (timeAtEndOfDay >= request.getDuration()) {
          TimeRange option = TimeRange.fromStartDuration(lastEventEnd, timeAtEndOfDay);
          options.add(option);
      }
    return options;
  }
}
