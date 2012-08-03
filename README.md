photon-core
===========

On the fly photo processing

Photon-core provides a JAX-RS resource (`com.thousandmemories.photon.core.PhotoResource`) that processes (resizes, rotates, and crops) image files on the fly.

Read more about why we built it on our blog post (TODO: link blog post after it's live) or see it in use in an example at https://github.com/1000Memories/photon-example


## PhotoResource
`PhotoResource` takes a path like `/1234.jpg;w=200;r=180;c=130,60,200,200`, fetches the image named `1234.jpg` using a `PhotoProvider`, and then processes the image according the matrix parameters.

### Matrix parameters

- `w=200`: Resizes the image to be 200px wide
- `r=90`: Rotates the image 90 degrees clockwise (`90`, `180`, and `270` are the available rotation angles)
- `c=130,60,200,300`: Crops the image 130px from the left, 60px from the top, with a width of 200px and a height of 300px.


## PhotoProvider

`PhotoResource`'s contructor takes an instance of `PhotoProvider`, which is responsible for finding the image based on the name it's given and returning an `InputStream` that will yield its contents.
This will most often mean looking up a file in a blob store (e.g. S3 or a file system).

An example that fetches the the avatar for the named Twitter user:
```java
public class TwitterPhotoProvider implements PhotoProvider {
    @Override
    public InputStream getPhotoInputStream(String path) throws IOException {
        return new URL("https://api.twitter.com/1/users/profile_image?size=original&screen_name=" + path).openStream();
    }
}
```
