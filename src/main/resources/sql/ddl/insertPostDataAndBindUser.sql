-- step 1
-- insert some post data
INSERT INTO POTATO.Post (post_title, post_content, post_image, post_genre, image_width, image_height) VALUES
-- First set of images (sculpture/3D art)
("The Sculptor's Dream", "Modern sculpture exploring form and space", "https://images.unsplash.com/photo-1682200736161-77f04daf9a59", "Sculpture", 3000, 2000),
("Abstract Thoughts", "Contemporary sculptural expression", "https://plus.unsplash.com/premium_photo-1679929943775-ee5b985e54f0", "Abstract Expressionism", 3000, 2000),
("Geometric Harmony", "Minimalist sculpture study", "https://images.unsplash.com/photo-1722970651091-cba52c42a789", "Sculpture", 3000, 2000),
("Digital Forms", "3D art meets traditional sculpture", "https://plus.unsplash.com/premium_photo-1679929943984-35525e89d8a6", "Mixed Media", 3000, 2000),
("Future Visions", "Surrealist sculptural exploration", "https://plus.unsplash.com/premium_photo-1721404752755-56f7b4a34068", "Surrealism", 3000, 2000),
("Material Study", "Mixed media sculptural work", "https://images.unsplash.com/photo-1722970651149-372e8c8a0109", "Mixed Media", 3000, 2000),
("Modern Classics", "Contemporary interpretation of classical forms", "https://plus.unsplash.com/premium_photo-1674946205706-cef99f9e0483", "Sculpture", 3000, 2000),
("Light and Shadow", "Photographic study of form", "https://images.unsplash.com/photo-1709035347321-1af386b38330", "Photography", 3000, 2000),
("Spatial Relations", "Abstract sculptural composition", "https://images.unsplash.com/photo-1709035350039-800825f4f2db", "Abstract Expressionism", 3000, 2000),
("Form Studies", "Experimental sculpture series", "https://images.unsplash.com/photo-1695742969355-99c507f57ec", "Sculpture", 3000, 2000),
("Digital Dreams", "3D art exploration", "https://images.unsplash.com/photo-1720184734699-b68794ffff95", "Mixed Media", 3000, 2000),
("Classical Remix", "Modern take on classical sculpture", "https://images.unsplash.com/photo-1722970651121-6a3ea5666ff7", "Sculpture", 3000, 2000),
("Material World", "Mixed media exploration", "https://images.unsplash.com/photo-1713007963097-2e5a7305178d", "Mixed Media", 3000, 2000),
("Future Forms", "Digital sculpture study", "https://images.unsplash.com/photo-1707583165631-ace8bedb3e70", "Mixed Media", 3000, 2000),
("Light Study", "Photographic exploration of form", "https://images.unsplash.com/photo-1709035347411-657a29bb13d7", "Photography", 3000, 2000),
("Abstract Reality", "Contemporary sculptural forms", "https://images.unsplash.com/photo-1702252290459-711fcbe57298", "Abstract Expressionism", 3000, 2000),
("Modern Vision", "Experimental sculpture", "https://images.unsplash.com/photo-1695742968496-2b0127f103cc", "Sculpture", 3000, 2000),

-- Second set of images (general art)
("The Color of Life", "Impressionist color study", "https://images.unsplash.com/photo-1579541814924-49fef17c5be5", "Impressionism", 3000, 2000),
("Digital Renaissance", "Modern graphic design", "https://plus.unsplash.com/premium_photo-1663937576065-706a8d985379", "Graphic Design", 3000, 2000),
("Abstract Thoughts", "Contemporary abstract exploration", "https://plus.unsplash.com/premium_photo-1676668708126-39b12a0e9d96", "Abstract Expressionism", 3000, 2000),
("Color Theory", "Fauvist color exploration", "https://plus.unsplash.com/premium_photo-1672287578309-2a2115000688", "Fauvism", 3000, 2000),
("Urban Canvas", "Street art documentation", "https://images.unsplash.com/photo-1578301978018-3005759f48f7", "Photography", 3000, 2000),
("Romantic Visions", "Contemporary romantic style", "https://images.unsplash.com/photo-1533158326339-7f3cf2404354", "Romanticism", 3000, 2000),
("Light Studies", "Photographic exploration", "https://images.unsplash.com/photo-1577083165633-14ebcdb0f658", "Photography", 3000, 2000),
("Modern Expression", "Contemporary painting", "https://images.unsplash.com/photo-1515405295579-ba7b45403062", "Painting", 3000, 2000),
("Abstract Reality", "Modern abstract work", "https://images.unsplash.com/photo-1459908676235-d5f02a50184b", "Abstract Expressionism", 3000, 2000),
("Color Field", "Abstract color study", "https://images.unsplash.com/photo-1577084381314-cae9920e6871", "Abstract Expressionism", 3000, 2000),
("Urban Dreams", "City photography", "https://images.unsplash.com/photo-1579541513287-3f17a5d8d62c", "Photography", 3000, 2000),
("Digital Age", "Contemporary digital art", "https://plus.unsplash.com/premium_photo-1682125164600-e7493508e496", "Graphic Design", 3000, 2000),
("Classical Forms", "Baroque-inspired work", "https://images.unsplash.com/flagged/photo-1572392640988-ba48d1a74457", "Baroque", 3000, 2000),
("Modern Times", "Contemporary mixed media", "https://images.unsplash.com/photo-1471666875520-c75081f42081", "Mixed Media", 3000, 2000),
("Light and Shadow", "Photographic study", "https://images.unsplash.com/photo-1577084381380-3b9ea4153664", "Photography", 3000, 2000),
("Urban Canvas", "Street photography", "https://images.unsplash.com/photo-1579762715118-a6f1d4b934f1", "Photography", 3000, 2000),
("Abstract Movement", "Modern abstract exploration", "https://images.unsplash.com/photo-1577083552792-a0d461cb1dd6", "Abstract Expressionism", 3000, 2000);


-- step 2
-- create some user account manually from page. The number of account must greater than 6

-- step 3
-- Start transaction
START TRANSACTION;

-- Insert UserPost relationships, distributing posts evenly among users
INSERT INTO POTATO.UserPost (user_id, post_id)
SELECT
    -- Distribute posts among users 1-6 using modulo
    CASE MOD(ROW_NUMBER() OVER (ORDER BY p.id), 6)
        WHEN 0 THEN 6
        WHEN 1 THEN 1
        WHEN 2 THEN 2
        WHEN 3 THEN 3
        WHEN 4 THEN 4
        WHEN 5 THEN 5
        END as user_id,
    p.id as post_id
FROM POTATO.Post p
WHERE NOT EXISTS (
    SELECT 1 FROM POTATO.UserPost up
    WHERE up.post_id = p.id
);

COMMIT;

-- Verify distribution
SELECT user_id, COUNT(*) as post_count
FROM POTATO.UserPost
GROUP BY user_id
ORDER BY user_id;