package nus.iss.team3.backend.service.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import nus.iss.team3.backend.domainService.user.IUserAccountService;
import nus.iss.team3.backend.entity.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired private JwtUtil jwtUtil;

  @Autowired private IUserAccountService userAccountService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    // to handle internal calls. dont need check if this is true...
    final String internalCall = request.getHeader("Internal-Service-Call");
    if (internalCall != null && internalCall.equals("true")) {

      UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
          new UsernamePasswordAuthenticationToken("internalSoOk", null, Collections.emptyList());
      usernamePasswordAuthenticationToken.setDetails(
          new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      chain.doFilter(request, response);
      return;
    }

    // get jwt token and get username in the token, cross check with the userId passed in to
    // validate the request.
    String authorizationHeader = request.getHeader("Authorization");

    int userId = -1;
    String jwt = null;

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwt = authorizationHeader.substring(7);
    }
    if (request.getHeader("userId") != null && !request.getHeader("userId").isEmpty()) {
      userId = Integer.parseInt(request.getHeader("userId"));
    }
    if (userId >= 1 && SecurityContextHolder.getContext().getAuthentication() == null) {

      UserAccount userAccount = userAccountService.getUserById(userId);

      if (jwtUtil.validateToken(jwt, userAccount)) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(userAccount, null, Collections.emptyList());
        usernamePasswordAuthenticationToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      }
    }
    chain.doFilter(request, response);
  }
}
