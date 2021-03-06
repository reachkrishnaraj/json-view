package com.monitorjbl.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonViewSupportFactoryBean implements InitializingBean {

  @Autowired
  private RequestMappingHandlerAdapter adapter;
  private final JsonViewMessageConverter converter;

  public JsonViewSupportFactoryBean() {
    this(new ObjectMapper());
  }

  public JsonViewSupportFactoryBean(ObjectMapper mapper) {
    this.converter = new JsonViewMessageConverter(mapper.copy());
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>(adapter.getReturnValueHandlers());
    adapter.setMessageConverters(Collections.<HttpMessageConverter<?>>singletonList(converter));
    decorateHandlers(handlers);
    adapter.setReturnValueHandlers(handlers);
  }

  private void decorateHandlers(List<HandlerMethodReturnValueHandler> handlers) {
    for (HandlerMethodReturnValueHandler handler : handlers) {
      if (handler instanceof RequestResponseBodyMethodProcessor) {
        int index = handlers.indexOf(handler);
        List<HttpMessageConverter<?>> converters = new ArrayList<>(adapter.getMessageConverters());
        converters.add(converter);
        handlers.set(index, new JsonViewReturnValueHandler(converters));
        break;
      }
    }
  }

}