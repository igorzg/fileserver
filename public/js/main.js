$(function () {
    $(".dimmer").dimmer({
        onChange: (step, deg) => {
            console.log("CHANGE", step, deg);
            // DO SOME HTTP REQUEST
        },
        onChangeStart: (step, deg) => {
            console.log("START", step, deg);
            // DO SOME HTTP REQUEST
        },
        onChangeEnd: (step, deg) => {
            console.log("END", step, deg);
            // DO SOME HTTP REQUEST
        },
        steps: 4,
        numbersAngle: 180,
        offText: "OFF",
        maxText: "MAX"
    });
});