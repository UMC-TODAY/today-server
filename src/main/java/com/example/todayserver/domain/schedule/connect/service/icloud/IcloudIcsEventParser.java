package com.example.todayserver.domain.schedule.connect.service.icloud;

import com.example.todayserver.domain.schedule.connect.dto.ExternalEventDto;
import com.example.todayserver.global.common.exception.CustomException;
import com.example.todayserver.global.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@org.springframework.stereotype.Component
public class IcloudIcsEventParser {

    public List<ExternalEventDto> parse(String icsText, LocalDateTime from, LocalDateTime to) {
        try {
            var calendar = new CalendarBuilder().build(new StringReader(icsText));
            ZoneId zone = ZoneId.of("Asia/Seoul");

            LocalDate fromDate = from.toLocalDate();
            LocalDate toDate = to.toLocalDate();

            List<ExternalEventDto> results = new ArrayList<>();

            @SuppressWarnings("unchecked")
            List<VEvent> events = calendar.getComponents(Component.VEVENT);

            for (VEvent e : events) {

                DtStart start = e.getStartDate();
                if (start == null) continue;

                DtEnd end = e.getEndDate();
                boolean allDay = !(start.getDate() instanceof DateTime);

                LocalDateTime startedAt;
                LocalDateTime endedAt;

                if (allDay) {
                    LocalDate sd = Instant.ofEpochMilli(start.getDate().getTime())
                            .atZone(zone).toLocalDate();

                    LocalDate ed = end != null
                            ? Instant.ofEpochMilli(end.getDate().getTime())
                            .atZone(zone).toLocalDate()
                            : sd.plusDays(1);

                    if (ed.isBefore(fromDate) || sd.isAfter(toDate)) continue;

                    startedAt = sd.atStartOfDay();
                    endedAt = ed.atStartOfDay();
                } else {
                    startedAt = Instant.ofEpochMilli(start.getDate().getTime())
                            .atZone(zone).toLocalDateTime();

                    endedAt = end != null
                            ? Instant.ofEpochMilli(end.getDate().getTime())
                            .atZone(zone).toLocalDateTime()
                            : null;

                    if (startedAt.isAfter(to) || (endedAt != null && endedAt.isBefore(from))) continue;
                }

                String uid = e.getUid() != null
                        ? e.getUid().getValue()
                        : UUID.randomUUID().toString();

                String title = e.getSummary() != null
                        ? e.getSummary().getValue()
                        : "(제목 없음)";

                String desc = e.getDescription() != null
                        ? e.getDescription().getValue()
                        : null;

                LocalDateTime updatedAt = null;
                if (e.getLastModified() != null) {
                    updatedAt = Instant.ofEpochMilli(e.getLastModified().getDate().getTime())
                            .atZone(zone).toLocalDateTime();
                }

                String versionKey = sha256(uid + "|" + title + "|" + startedAt + "|" + endedAt);

                results.add(new ExternalEventDto(
                        uid + "#" + startedAt,
                        title,
                        desc,
                        startedAt,
                        endedAt,
                        allDay,
                        updatedAt,
                        versionKey,
                        null
                ));
            }

            return results;

        } catch (Exception e) {
            throw new CustomException(ErrorCode.EXTERNAL_CALENDAR_PARSE_FAILED);
        }
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return input;
        }
    }
}
