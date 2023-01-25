// package com.backend.seperate.controller;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


// @SpringBootTest
// // @AutoConfigureMockMvc
// public class UserControllerTest {

//   @Autowired
//   MockMvc mvc;

//   @BeforeEach
//   public void setup() throws Exception{
//     System.out.println("JUNIT BeforeEach");    
//   }

//   @Test
//   @DisplayName("이메일 존재여부 테스트")
//   void testIsExistsEmail() throws Exception{
//     mvc.perform(
//       MockMvcRequestBuilders
//         .post("/sign/existsEmail")
//           .contentType(MediaType.APPLICATION_JSON)
//           .characterEncoding("UTF-8")
//           .content(
//             "{"
//           +" \"email\" : \"admin@google.co.kr\" "
//           +"}")
//           )
//           .andExpect(MockMvcResultMatchers.status().isOk()) // Response 처리 방법
//           .andDo(MockMvcResultHandlers.print()); // 해당 테스트의 전반적인 내용을 보여줌.
//   }
// }
