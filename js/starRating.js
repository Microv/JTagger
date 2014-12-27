$(function() {
	$('.stars').stars();
});

$.fn.stars = function() {
	return $(this).each(function() {

		// Get the value
		var value = parseFloat($(this).html());

		// Make sure that the value is in 0 - 5 range, multiply to get width
		var size = Math.max(0, Math.min(5, value)) * 16;

		// Create star holder
		var $span = $('<span />').width(size);

		// Replace the numeric value with stars
		$(this).html($span);
	});
}