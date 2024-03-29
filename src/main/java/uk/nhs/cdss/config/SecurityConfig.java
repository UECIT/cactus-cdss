package uk.nhs.cdss.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import uk.nhs.cactus.common.security.JWTFilter;

@Configuration
@RequiredArgsConstructor
@ComponentScan("uk.nhs.cactus.common.security")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final JWTFilter jwtFilter;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .httpBasic().disable()
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        // TODO CDSCT-233
        .antMatchers(HttpMethod.OPTIONS, "/image/**").permitAll()
        .antMatchers(HttpMethod.GET, "/image/**").permitAll()
        .anyRequest().authenticated()
        .and()
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
  }
}
