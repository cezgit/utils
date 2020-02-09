package com.wds.util;


import com.wds.util.io.DownloadResponseUtil;
import org.jmock.Expectations;
import org.jmock.Mockery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class DownloadResponseUtilTest {
    private ServletOutputStream mockOutputStream;
    private HttpServletResponse mockResponse;
    private Mockery context = new Mockery();

    @Before
    public void setUp() {
        mockOutputStream =context.mock(ServletOutputStream.class);
        mockResponse = context.mock(HttpServletResponse.class);
    }

    @Test
    public void testThatSendResponseInvokesExpectedMethodsOnResponseAndWritesTheOrderInquireiesToOutputStream() throws Exception {
        setUpExpectationsForResponse();
        new DownloadResponseUtil().sendResponse(mockResponse, "OrderInquiries", "archivedOrderInquiries.csv");
    }

     private void setUpExpectationsForResponse() throws IOException {
        context.checking(new Expectations() {
            {
                oneOf(mockResponse).reset();

                oneOf(mockResponse).setContentType("application/octet-stream");

                oneOf(mockResponse).setHeader("Content-Disposition", "attachment; filename=\"archivedOrderInquiries.csv\"");

                oneOf(mockResponse).getOutputStream();
                will(returnValue(mockOutputStream));

                oneOf(mockOutputStream).write("OrderInquiries".getBytes());
            }
        });
    }

    @After
    public void assertThatAllTheExpectedMethodsWereExecuted() {
        context.assertIsSatisfied();
    }


}
