package com.pazzioliweb.commonbacken.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pazzioliweb.commonbacken.dtos.DatosSesiones;

//CONFIGURACION DE REDIS EN SPRING 
//esta clase configura  un RedisTemplate<String, DatosSesiones> que me permite guardar y recuperar objetos de la clase datos secciones desde
//redix de  forma segura y eficiente ,usando json como  formato serializacion
@Configuration
public class RedisConfig {
    //creo un redix con los valores serilizables adecuados
//consta de dos tipos genricos  Claves en texto (StringRedisSerializer) y Valores como JSON (Jackson2JsonRedisSerializer)

    //este redix permitira guardar objetos de la clase Datossesiones directamente en redix
//Define un bean de Spring llamado redisTemplate
    //Este template es el objeto que vas a usar para comunicarte con Redis
    //Es tipado: usas claves String y valores tipo DatosSesiones.
    @Bean
    public RedisTemplate<String, DatosSesiones> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, DatosSesiones> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Serializador para las claves (String)
        //Crea una nueva instancia de `RedisTemplate`.
        // Le pasa la **conexión a Redis**, que Spring Boot auto-configura con los datos de `application.properties` (`host`, `port`, etc.).
        ///Indica que las claves (keys) en Redis serán serializadas como strings normales (por ejemplo "token-usuario123").
        //Si no configuras esto, Redis puede almacenar los keys como binario ilegible.
        // Serializador de claves (Strings)

        template.setKeySerializer(new StringRedisSerializer());

        // --- Configuración de Jackson para valores ---
        ObjectMapper objectMapper = new ObjectMapper();
        // Permite serializar tipos de Java 8, como Instant
        objectMapper.registerModule(new JavaTimeModule());
        // Desactiva FAIL_ON_UNKNOWN_PROPERTIES si quieres ignorar campos extra
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Configuramos Jackson para serializar objetos (DatosSesiones)
        // Crea un `ObjectMapper` de Jackson, que permite convertir objetos Java ↔ JSON.
        // Se activa el "typing" para poder serializar y deserializar correctamente objetos que **no tienen tipo final** como `Instant`, listas, etc.



     /*   ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );
        objectMapper.registerModule(new JavaTimeModule());*/


        BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.pazzioliweb.commonbacken.dtos")
                .build();


        /*   objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);*/
        // Crea un serializador JSON específico para DatosSesiones.

        //  Usa el ObjectMapper que configuraste antes.
        // Crea el serializador de valores usando este ObjectMapper
        //Esto indica: "cuando guardes algo en Redis, convierte DatosSesiones en JSON usando Jackson".
//Esto es necesario porque Redis solo guarda bytes, y queremos convertir `DatosSesiones` automáticamente a JSON y luego a objeto Java.
        Jackson2JsonRedisSerializer<DatosSesiones> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper,DatosSesiones.class);




        // Serializador para los valores (DatosSesiones en JSON)
        // Define que los **valores** (los objetos `DatosSesiones`) serán serializados y deserializados como JSON.
        // También lo aplica a valores dentro de estructuras `Hash`.
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}