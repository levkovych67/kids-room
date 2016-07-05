package ua.softserveinc.tc.controller.user;

import com.google.gson.Gson;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.softserveinc.tc.dao.UserDao;
import ua.softserveinc.tc.dto.BookingDto;
import ua.softserveinc.tc.dto.PeriodDto;
import ua.softserveinc.tc.entity.BookingState;
import ua.softserveinc.tc.entity.Room;
import ua.softserveinc.tc.service.BookingService;
import ua.softserveinc.tc.service.ChildService;
import ua.softserveinc.tc.service.RoomService;
import ua.softserveinc.tc.util.DateUtil;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dima- on 06.06.2016.
 */
@Controller
public class BookingTimeController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ChildService childService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

    @RequestMapping(value = "gwtroomproperty", method = RequestMethod.POST)
    @ResponseBody
    public String getRoomProperty(@RequestBody Integer roomId){

        return null;
    }

    @RequestMapping(value = "makenewbooking", method = RequestMethod.POST)
    @ResponseBody
    public String getBooking(@RequestBody List<BookingDto> dtos) {
        dtos.forEach(dto -> {
            dto.setUser(userDao.findById(dto.getUserId()));
            dto.setChild(childService.findById(dto.getKidId()));
            dto.setRoom(roomService.findById(dto.getRoomId()));
            dto.setBookingState(BookingState.BOOKED);
            dto.setKidName(childService.findById(dto.getKidId()).getFullName());
            dto.setDateStartTime(DateUtil.toDateISOFormat(dto.getStartTime()));
            dto.setDateEndTime(DateUtil.toDateISOFormat(dto.getEndTime()));
        });

        List<BookingDto> dto = bookingService.persistBookingsFromDtoAndSetId(dtos);
        if (dto != null) {
            return new Gson().toJson(dto);
        }
        return null;
    }

    @RequestMapping(value = "getallbookings/{idUser}/{idRoom}",
            method = RequestMethod.GET,
            produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String getAllBookings(@PathVariable Long idUser,
                                 @PathVariable Long idRoom) {
        return new Gson().toJson(bookingService.getAllBookingsByUserAndRoom(idUser, idRoom));
    }

    @RequestMapping(value = "/disabled", method = RequestMethod.GET)
    @ResponseBody
    public String getDisabledTime(@RequestParam Long roomID,
                                  @RequestParam String dateLo,
                                  @RequestParam String dateHi) {
        Room room = roomService.findById(roomID);

        Calendar start = Calendar.getInstance();
        start.setTime(DateUtil.toDate(dateLo));

        Calendar end = Calendar.getInstance();
        end.setTime(DateUtil.toDate(dateHi));

        return new Gson().toJson(roomService.getBlockedPeriods(room, start, end)
                    .stream()
                    .map(PeriodDto::toJson)
                    .collect(Collectors.toList()));
    }

    @RequestMapping(value = "getrecurrentbookings", method = RequestMethod.POST)
    @ResponseBody
    public String makeRecurrentBookings(@RequestBody List<BookingDto> bookingDtos) {
        for (BookingDto bookingDto : bookingDtos) {
            if(bookingDto.getIdChild() == null) {
                bookingDto.setIdChild(bookingDto.getKidId());
            }

            if(bookingDto.getKidId() == null) {
                bookingDto.setKidId(bookingDto.getIdChild());
            }
        }
        List<BookingDto> bookings = bookingService.makeRecurrentBookings(bookingDtos);

        return  new Gson().toJson(bookings);
    }

}
