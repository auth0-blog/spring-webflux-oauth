package com.example.movieapi.controller;

import com.example.movieapi.model.Movie;
import com.example.movieapi.model.repository.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository){
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public Flux<Movie> getMovies(){
        return movieRepository.findAll();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Movie>> getMovie(@PathVariable String id){
        return movieRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Movie> saveMovie(@RequestBody Movie movie){
        return movieRepository.save(movie);
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Movie>> updateMovie(@PathVariable(value = "id") String id, @RequestBody Movie movie){
        return movieRepository.findById(id)
                .flatMap(existingMovie -> {
                    existingMovie.setName(movie.getName());
                    existingMovie.setGenre(movie.getGenre());
                    existingMovie.setReleaseDate(movie.getReleaseDate());
                    return movieRepository.save(existingMovie);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteMovie(@PathVariable(value = "id") String id){
        return movieRepository.findById(id)
                .flatMap(existingMovie ->
                        movieRepository.delete(existingMovie)
                                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public Mono<Void> deleteAllMovies(){
        return movieRepository.deleteAll();
    }
}