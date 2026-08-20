package com.nag.exception;

import java.time.LocalDateTime;

/**
 * Error response body sent back to the client when a request fails.
 *
 * @param status    HTTP status code
 * @param message   reason the request failed
 * @param timestamp when the error occurred
 */

public record ErrorResponse(int status, String message, LocalDateTime timestamp) {
}
