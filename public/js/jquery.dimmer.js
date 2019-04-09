(function ($) {

    class Dimmer {
        constructor(options, selector) {

            this._mouseDown = false;
            this._mPos = {
                x: 0,
                y: 0
            };

            this._settings = $.extend({
                onChange: (ang, deg) => {
                },
                onChangeStart: (ang, deg) => {
                },
                onChangeEnd: (ang, deg) => {
                },
                elementPosition: {
                    x: 0,
                    y: 0
                },
                target: 0,
                steps: 4,
                radius: 93,
                numberRadius: 26,
                numbersAngle: 180,
                maxDiff: 150,
                constraint: 360,
                maxAngle: 250,
                offText: "OFF",
                maxText: "MAX"
            }, options);

            this._$selector = $(selector);
            this._$body = $("body");
            this._$lump = this._$selector.find(".lump");
            this._$progress = this._$selector.find(".progress");
            this._$ctx = this._$progress.get(0).getContext("2d");
            const offset = this._$lump.offset();
            this._settings.elementPosition = {
                x: offset.left,
                y: offset.top
            };

            this._centerX = this._$progress.width() / 2;
            this._centerY = this._$progress.height() / 2;
            this._canvasSize = this._$progress.width();
            this._$selector.on("mousedown", () => {
                let angle = this._getCurrentAngle();
                let deg = this._getCurrentDeg();
                if (angle > 0) {
                    this._settings.onChangeStart(Math.round(Math.min(Math.max(angle, 1), 100)), deg);
                } else {
                    this._settings.onChangeStart(0, deg);
                }
                this._mouseDown = true;
            });
            this._$selector.on("mousemove", () => {
                if (this._mouseDown) {
                    this._setMousePosition(event);
                }
            });
            this._$body.on("mouseup", () => {
                this._mouseDown = false;
                let angle = this._getCurrentAngle();
                let deg = this._getCurrentDeg();
                if (angle > 0) {
                    this._settings.onChangeEnd(Math.round(Math.min(Math.max(angle, 1), 100)), deg);
                } else {
                    this._settings.onChangeEnd(0, deg);
                }
            });

            this._draw();

        }

        /**
         * Draw angle
         * @param endAngle
         * @private
         */
        _drawLine(endAngle) {
            this._$ctx.save();
            this._$ctx.translate(this._centerX, this._centerY);
            this._$ctx.rotate(145 * (Math.PI / 180));
            let startAngle = 0;
            let x = 0;
            let y = 0;
            this._$ctx.moveTo(98, 0);
            this._$ctx.beginPath();
            this._$ctx.shadowBlur = 10;
            this._$ctx.lineWidth = 2.4;
            this._$ctx.strokeStyle = "#fffdcf";
            this._$ctx.shadowBlur = 10;
            this._$ctx.shadowColor = "#fff";
            this._$ctx.arc(x, y, this._settings.radius, startAngle, endAngle, false);
            this._$ctx.stroke();
            this._$ctx.beginPath();
            this._$ctx.strokeStyle = "#7f7f7f";
            this._$ctx.shadowBlur = 0;
            this._$ctx.arc(x, y, this._settings.radius, endAngle, this._settings.maxAngle * Math.PI / 180, false);
            this._$ctx.stroke();
            return this._$ctx.restore();
        }

        /**
         * Drow steps
         * @private
         */
        _drawSteps() {
            this._$ctx.save();
            this._$ctx.translate(this._centerX, this._centerY);
            this._$ctx.rotate(135 * Math.PI / 180);
            for (let i = 0, j = 0, ref = this._settings.steps; j <= ref; i = j += 1) {
                this._$ctx.beginPath();
                this._$ctx.rotate(180 * Math.PI / 180 / this._settings.steps);
                this._$ctx.strokeStyle = "#7f7f7f";
                this._$ctx.lineWidth = 2;
                this._$ctx.lineTo(108, 0);
                this._$ctx.lineTo(100, 0);
                this._$ctx.stroke();
            }
            this._$ctx.restore();
        }

        /**
         * Draw numbers
         * @private
         */
        _drawNumbers() {
            let angle = 180 * (Math.PI / this._settings.numbersAngle);
            let step = 180 * Math.PI / 180 / this._settings.steps;
            let radius = this._settings.radius + this._settings.numberRadius;
            this._$ctx.translate(this._centerX, this._centerY);
            this._$ctx.save();
            for (let i = 0, j = 0, ref = this._settings.steps; j <= ref; i = j += 1) {
                let x = radius * Math.cos(angle) - 4;
                let y = radius * Math.sin(angle) + 4;
                angle += step;
                this._$ctx.fillStyle = "#7f7f7f";
                this._$ctx.font = "bold 13px Arial";
                this._$ctx.fillText(i + 1, x, y);
            }
            this._$ctx.restore();
            this._$ctx.fillStyle = "#636262";
            this._$ctx.font = "normal 12px Arial";
            this._$ctx.fillText(this._settings.offText, -84, 75);
            this._$ctx.fillText(this._settings.maxText, 62, 75);
        }

        /**
         * Draw canvas
         * @private
         */
        _draw() {
            this._$progress.get(0).height = this._canvasSize;
            this._$progress.get(0).width = this._canvasSize;
            this._drawLine(this._settings.target * Math.PI / 180);
            this._drawSteps();
            this._drawNumbers();
            this._updateBackground();
        }

        /**
         * Update background with gradient
         * @private
         */
        _updateBackground() {
            let normalizedTarget = this._map(this._settings.target, 0, this._settings.maxAngle, 0, 1);
            let gray = parseInt(normalizedTarget * 255, 10);
            this._$body.css({
                background: `#000 radial-gradient(ellipse at center, #8c8f95 0%, rgb(${gray},${gray},${gray}) 100%) center center no-repeat`
            });

        }

        /**
         * Get current deg
         * @returns {number}
         * @private
         */
        _getCurrentDeg() {
            let ang = Math.atan2(this._mPos.x, this._mPos.y);
            if (ang < 0) {
                ang = ang + 2 * Math.PI;
            }
            return 360 - ang * (180 / Math.PI);
        }

        /**
         * Get current Angle
         * @returns {number}
         * @private
         */
        _getCurrentAngle() {
            let deg = this._getCurrentDeg();
            return Math.round(((deg - 40) * 100) / 300);
        }

        /**
         * Set mouse position
         * @param event
         * @private
         */
        _setMousePosition(event) {
            this._mPos = {
                x: event.pageX - (this._settings.elementPosition.x + this._centerX),
                y: event.pageY - (this._settings.elementPosition.y + this._centerY)
            };

            let deg = this._getCurrentDeg();
            let target = this._map(deg, 0, 360, -40, 270);
            let diff = Math.abs(target - this._settings.target);
            if (diff < this._settings.maxDiff && target < this._settings.constraint) {
                this._settings.target = target;
                if (this._settings.target > this._settings.maxAngle) {
                    this._settings.target = this._settings.maxAngle;
                }
                if (this._settings.target < 0) {
                    this._settings.target = 0;
                }
            }
            this._draw();

            let angle = this._getCurrentAngle();
            if (angle > 0) {
                this._settings.onChange(Math.round(Math.min(Math.max(angle, 1), 100)), deg);
            } else {
                this._settings.onChange(0, deg);
            }
        }

        /**
         * Map to target
         * @param value
         * @param low1
         * @param high1
         * @param low2
         * @param high2
         * @returns {*}
         * @private
         */
        _map(value, low1, high1, low2, high2) {
            return low2 + (high2 - low2) * (value - low1) / (high1 - low1);
        }
    }


    $.fn.dimmer = $.extendable(Dimmer);
}(jQuery));