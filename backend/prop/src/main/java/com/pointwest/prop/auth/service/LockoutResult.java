package com.pointwest.prop.auth.service;

public record LockoutResult(boolean justLocked, long lockDurationMinutes) {
}