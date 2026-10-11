package com.portfolio.Servlets.ChatServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import com.portfolio.Service.ChatService;
import com.portfolio.model.Admin;
import com.portfolio.model.Chat;
import com.portfolio.model.Users;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/chat")
public class SendMessageServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        HttpSession session = req.getSession();
        Admin loggedInAdmin = (Admin) session.getAttribute("loggedInAdmin");
        Users senderUser = (Users) session.getAttribute("loggedInAdmin");

        if(loggedInAdmin == null && senderUser == null){
            return;
        }

        Chat chat = new Chat();
        chat.setMessage("message");
        chat.setSender("sender");
        chat.setReciever("reciever");
        chat.setTime(LocalDateTime.now());

        ChatService chatSer = new ChatService();
        PrintWriter out = resp.getWriter();

        //if sender is admin
        if(chat.getSender() == loggedInAdmin.getUsername()){
            Users user = new Users();
            user.setUsername("username");
            if(chatSer.sendChat(chat)){
                resp.setStatus(201);
                out.println("sent");
            }
            else{
                resp.setStatus(500);
                out.println("not sent");
            }
        }
        //if sender is user
        if(chat.getSender() == senderUser.getUsername()){
            if(chatSer.sendChat(chat)){
                resp.setStatus(201);
                out.println("sent");
            }
            else{
                resp.setStatus(500);
                out.println("not sent");
            }
        }
    }
}
