package com.lzl.fbsse.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.lzl.fbsse.data.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BookingTools {

	private static final Logger logger = LoggerFactory.getLogger(BookingTools.class);

	@Autowired
	private FlightBookingService flightBookingService;

	public record BookingDetailsRequest(String bookingNumber, String name) {
	}

	public record ChangeBookingDatesRequest(String bookingNumber, String name, String date, String from, String to) {
	}

	public record CancelBookingRequest(@ToolParam(description = "预定号")
									   String bookingNumber,
									   @ToolParam(description = "姓名")
									   String name) {
	}

	@JsonInclude(Include.NON_NULL)
	public record BookingDetails(String bookingNumber, String name, LocalDate date, BookingStatus bookingStatus,
			String from, String to, String bookingClass) {
	}


	@Tool(description = "获取机票预定详细信息")
	BookingDetails getBookingDetails( @ToolParam(description = "预定号")
									  String bookingNumber,
									  @ToolParam(description = "姓名")
									  String name) {
		try {
			return flightBookingService.getBookingDetails(bookingNumber,name);
		}
		catch (Exception e) {
			logger.warn("Booking details: {}", NestedExceptionUtils.getMostSpecificCause(e).getMessage());
			return new BookingDetails(bookingNumber, name, null, null, null, null, null);
		}
	}


//	@Tool(description = "修改机票预定日期")
//	String changeBooking(ChangeBookingDatesRequest bookingDetailsRequest) {
//		flightBookingService.changeBooking(bookingDetailsRequest.bookingNumber(), bookingDetailsRequest.name(),
//				bookingDetailsRequest.date(), bookingDetailsRequest.from(),bookingDetailsRequest.to());
//		return "";
//	}

	@Tool(description = "取消机票预定")
	String cancelBooking(@ToolParam(description = "预定号")
						 String bookingNumber,
						 @ToolParam(description = "姓名")
						 String name) {
		flightBookingService.cancelBooking(bookingNumber,
				name );

		return "";
	}


	/*@Bean
	@Description("获取机票预定详细信息")
	public Function<BookingDetailsRequest, BookingDetails> getBookingDetails() {
		return request -> {
			try {
				return flightBookingService.getBookingDetails(request.bookingNumber(), request.name());
			}
			catch (Exception e) {
				logger.warn("Booking details: {}", NestedExceptionUtils.getMostSpecificCause(e).getMessage());
				return new BookingDetails(request.bookingNumber(), request.name(), null, null, null, null, null);
			}
		};
	}

	@Bean
	@Description("修改机票预定日期")
	public Function<ChangeBookingDatesRequest, String> changeBooking() {
		return request -> {
			flightBookingService.changeBooking(request.bookingNumber(), request.name(), request.date(), request.from(),
					request.to());
			return "";
		};
	}

	@Bean
	@Description("取消机票预定")
	public Function<CancelBookingRequest, String> cancelBooking() {
		return request -> {
			flightBookingService.cancelBooking(request.bookingNumber(), request.name());
			return "";
		};
	}*/

}
