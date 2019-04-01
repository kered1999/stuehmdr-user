package com.cybr406.user.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@Configuration
public class Oauth2ServerConfig {


        @Configuration
        @EnableResourceServer
        protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

            @Override
            public void configure(HttpSecurity http) throws Exception {
                // @formatter:off
                http
                        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .and()
                        .requestMatchers().antMatchers("/profiles/**", "/profiles", "/posts/**","/posts","/signup")
                        .and()
                        .authorizeRequests()
//                        .antMatchers("/me").access("#oauth2.hasScope('read')")
//                        .antMatchers("/photos").access("#oauth2.hasScope('read') or (!#oauth2.isOAuth() and hasRole('ROLE_USER'))")
//                        .antMatchers("/photos/trusted/**").access("#oauth2.hasScope('trust')")
//                        .antMatchers("/photos/user/**").access("#oauth2.hasScope('trust')")
//                        .antMatchers("/photos/**").access("#oauth2.hasScope('read') or (!#oauth2.isOAuth() and hasRole('ROLE_USER'))")
                        .regexMatchers(HttpMethod.DELETE, "/oauth/users/([^/].*?)/tokens/.*")
                        .access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('write')")
                        .regexMatchers(HttpMethod.GET, "/oauth/clients/([^/].*?)/users/.*")
                        .access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('read')")
                        .regexMatchers(HttpMethod.GET, "/oauth/clients/.*")
                        .access("#oauth2.clientHasRole('ROLE_CLIENT') and #oauth2.isClient() and #oauth2.hasScope('read')");
                // @formatter:on
            }

        }

        @Configuration
        @EnableAuthorizationServer
        protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

            @Autowired
            private TokenStore tokenStore;



            @Autowired
            private AuthenticationManager authenticationManager;

            @Autowired
            PasswordEncoder passwordEncoder;


            @Override
            public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

                // @formatter:off
                clients.inMemory()

                        .withClient("api")
                            .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                            .authorities("ROLE_BLOGGER")
                            .scopes("read", "write", "trust")
                            .secret(passwordEncoder.encode(""))
                            .and()
                        .withClient("post")
                            .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                            .authorities("ROLE_BLOGGER")
                            .scopes("read", "write", "trust")
                            .accessTokenValiditySeconds(60)
                            .secret(passwordEncoder.encode("secret"));
                // @formatter:on
            }

            @Bean
            public TokenStore tokenStore() {
                return new InMemoryTokenStore();
            }

            @Override
            public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
                endpoints.tokenStore(tokenStore).authenticationManager(authenticationManager);
            }

            @Override
            public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
                oauthServer.realm("/oauth/check_token");
            }
        }


        protected static class Stuff {

            @Autowired
            private ClientDetailsService clientDetailsService;

            @Autowired
            private TokenStore tokenStore;

            @Bean
            public ApprovalStore approvalStore() throws Exception {
                TokenApprovalStore store = new TokenApprovalStore();
                store.setTokenStore(tokenStore);
                return store;
            }

        }


}
