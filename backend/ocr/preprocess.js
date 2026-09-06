const sharp = require("sharp");
const path = require("path");

const input = path.join(
    __dirname,
    "..",
    "src",
    "ocr",
    "input",
    "test.png"
);

const output = path.join(
    __dirname,
    "..",
    "src",
    "ocr",
    "input",
    "preprocessed.png"
);

async function preprocess() {
    try {
        console.log("Starting image preprocessing...");
        console.log("Input:", input);

        const metadata = await sharp(input).metadata();

        console.log(
            `Original size: ${metadata.width} x ${metadata.height}`
        );

        const newWidth = Math.round(metadata.width * 2.5);

        await sharp(input)
            .rotate()
            .resize({
                width: newWidth,
                kernel: sharp.kernel.lanczos3
            })
            .grayscale()
            .normalize()
            .gamma(1.1)
            .sharpen({
                sigma: 2,
                m1: 1.5,
                m2: 3
            })
            .png()
            .toFile(output);

        console.log("Preprocessing complete!");
        console.log("Output:", output);

    } catch (error) {
        console.error("Image preprocessing failed:");
        console.error(error);
        process.exit(1);
    }
}

preprocess();
