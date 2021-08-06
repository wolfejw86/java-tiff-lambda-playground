package my.service.resource;

import sun.misc.BASE64Decoder;
import java.util.Map;
import java.util.Base64;
import java.util.HashMap;
import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ping")
public class PingResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public Response createPet() {
        Map<String, String> pong = new HashMap<>();
        pong.put("pong", "Hello, World!");
        return Response.status(200).entity(pong).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUpload(CustomUpload upload) {
        try {
            System.out.println(upload.image1.substring(0, 20));
            System.out.println(upload.image2.substring(0, 20));
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] imageByte1;
            byte[] imageByte2;

            imageByte1 = decoder.decodeBuffer(upload.getImage1());
            imageByte2 = decoder.decodeBuffer(upload.getImage2());

            InputStream is1 = new ByteArrayInputStream(imageByte1);
            BufferedImage image1 = ImageIO.read(is1);
            InputStream is2 = new ByteArrayInputStream(imageByte2);
            BufferedImage image2 = ImageIO.read(is2);
            System.out.println("Maybe got 2 images at this point");

            // ByteArrayInputStream bis = new ByteArrayInputStream(imageByte1);
            // ByteArrayInputStream bis = new ByteArrayInputStream(imageByte1);
            // image = ImageIO.read(bis);
            // bis.close();
            ImageWriter writer = ImageIO.getImageWritersByFormatName("TIFF").next();
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ImageOutputStream output = ImageIO.createImageOutputStream(byteOutput);
            writer.setOutput(output);

            ImageWriteParam params = writer.getDefaultWriteParam();
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionType("Deflate");

            writer.prepareWriteSequence(null);
            writer.writeToSequence(new IIOImage(image1, null, null), params);
            writer.writeToSequence(new IIOImage(image2, null, null), params);
            writer.endWriteSequence();
            writer.dispose();

            output.flush();

            String encoded = Base64.getEncoder().encodeToString(byteOutput.toByteArray());

            System.out.println(encoded.substring(0, 20));

            CombinedTiff ctiff = new CombinedTiff();
            ctiff.tiff = encoded;

            return Response.status(Response.Status.OK).entity(ctiff).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(upload).build();
        }

    }

    public BufferedImage getScaledImage(BufferedImage src, int w, int h) {
        int original_width = src.getWidth();
        int original_height = src.getHeight();
        int bound_width = w;
        int bound_height = h;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            // scale width to fit
            new_width = bound_width;
            // scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            // scale height to fit instead
            new_height = bound_height;
            // scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        BufferedImage resizedImg = new BufferedImage(new_width, new_height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, new_width, new_height);
        g2.drawImage(src, 0, 0, new_width, new_height, null);
        g2.dispose();
        return resizedImg;
    }
}
