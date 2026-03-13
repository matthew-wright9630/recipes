INSERT INTO users (id, name, email, password_hash, role)
VALUES
(1, 'Matthew Wright', 'matthew.wright9630@gmail.com', 'hashed_password', 'Admin'),
(2, 'Laura', 'matthew.laura.wright@gmail.com', 'hashed_password', 'User');

INSERT INTO recipes (id, name, description, servings, prep_time, cook_time, created_by)
VALUES
(1, 'Classic Pancakes', 'Fluffy homemade breakfast pancakes.', 4, 10, 10, 2),
(2, 'Spaghetti Bolognese', 'Classic Italian meat sauce pasta.', 4, 15, 45, 3),
(3, 'Chicken Caesar Salad', 'Crisp romaine with grilled chicken and Caesar dressing.', 2, 15, 10, 2),
(4, 'Chocolate Chip Cookies', 'Soft baked cookies loaded with chocolate chips.', 24, 15, 12, 4),
(5, 'Vegetable Omelette', 'Quick breakfast omelette with vegetables.', 1, 5, 5, 1);

INSERT INTO ingredients (name) VALUES

-- Dairy & Eggs
('egg'),
('egg white'),
('egg yolk'),
('egg substitute'),
('milk'),
('whole milk'),
('skim milk'),
('buttermilk'),
('heavy cream'),
('half and half'),
('sour cream'),
('cream cheese'),
('ricotta cheese'),
('cottage cheese'),
('mozzarella cheese'),
('cheddar cheese'),
('parmesan cheese'),
('feta cheese'),
('goat cheese'),
('yogurt'),
('greek yogurt'),
('butter'),
('unsalted butter'),
('salted butter'),
('margarine'),

-- Baking
('all-purpose flour'),
('bread flour'),
('cake flour'),
('pastry flour'),
('whole wheat flour'),
('almond flour'),
('coconut flour'),
('cornmeal'),
('cornstarch'),
('baking soda'),
('baking powder'),
('active dry yeast'),
('instant yeast'),
('granulated sugar'),
('brown sugar'),
('light brown sugar'),
('dark brown sugar'),
('powdered sugar'),
('vanilla extract'),
('almond extract'),
('cocoa powder'),
('chocolate chips'),
('dark chocolate'),
('milk chocolate'),
('white chocolate'),
('maple syrup'),
('honey'),
('molasses'),

-- Oils & fats
('olive oil'),
('extra virgin olive oil'),
('vegetable oil'),
('canola oil'),
('corn oil'),
('sesame oil'),
('coconut oil'),
('peanut oil'),
('shortening'),
('lard'),

-- Produce
('onion'),
('red onion'),
('yellow onion'),
('green onion'),
('garlic'),
('ginger'),
('carrot'),
('celery'),
('bell pepper'),
('red bell pepper'),
('green bell pepper'),
('yellow bell pepper'),
('jalapeno'),
('serrano pepper'),
('potato'),
('russet potato'),
('sweet potato'),
('tomato'),
('cherry tomato'),
('roma tomato'),
('spinach'),
('baby spinach'),
('kale'),
('lettuce'),
('romaine lettuce'),
('arugula'),
('broccoli'),
('cauliflower'),
('zucchini'),
('yellow squash'),
('cucumber'),
('mushroom'),
('white mushroom'),
('cremini mushroom'),
('portobello mushroom'),
('avocado'),
('corn'),
('peas'),
('green beans'),
('asparagus'),
('cabbage'),
('red cabbage'),
('brussels sprouts'),

-- Fruit
('apple'),
('banana'),
('orange'),
('lemon'),
('lime'),
('grapefruit'),
('strawberry'),
('blueberry'),
('raspberry'),
('blackberry'),
('pineapple'),
('mango'),
('peach'),
('pear'),
('cherry'),
('grape'),
('watermelon'),
('cantaloupe'),

-- Meat & Seafood
('chicken breast'),
('chicken thigh'),
('whole chicken'),
('ground chicken'),
('turkey breast'),
('ground turkey'),
('ground beef'),
('beef chuck'),
('beef steak'),
('beef roast'),
('pork chop'),
('pork tenderloin'),
('ground pork'),
('bacon'),
('ham'),
('sausage'),
('salmon'),
('tuna'),
('shrimp'),
('cod'),
('tilapia'),
('crab'),
('lobster'),

-- Grains & Pasta
('white rice'),
('brown rice'),
('jasmine rice'),
('basmati rice'),
('quinoa'),
('barley'),
('couscous'),
('bulgur'),
('oats'),
('rolled oats'),
('steel cut oats'),
('spaghetti'),
('linguine'),
('fettuccine'),
('penne'),
('macaroni'),
('egg noodles'),
('lasagna noodles'),
('breadcrumbs'),
('panko breadcrumbs'),

-- Beans & Legumes
('black beans'),
('kidney beans'),
('pinto beans'),
('navy beans'),
('chickpeas'),
('lentils'),
('split peas'),
('refried beans'),

-- Broths & Liquids
('chicken broth'),
('chicken stock'),
('beef broth'),
('beef stock'),
('vegetable broth'),
('vegetable stock'),
('water'),
('white wine'),
('red wine'),
('apple cider vinegar'),
('white vinegar'),
('balsamic vinegar'),
('rice vinegar'),
('soy sauce'),
('tamari'),
('worcestershire sauce'),
('hot sauce'),

-- Herbs
('basil'),
('parsley'),
('cilantro'),
('oregano'),
('thyme'),
('rosemary'),
('sage'),
('dill'),
('mint'),
('chives'),

-- Spices
('salt'),
('kosher salt'),
('sea salt'),
('black pepper'),
('white pepper'),
('garlic powder'),
('onion powder'),
('paprika'),
('smoked paprika'),
('chili powder'),
('cayenne pepper'),
('red pepper flakes'),
('cumin'),
('coriander'),
('turmeric'),
('curry powder'),
('cinnamon'),
('nutmeg'),
('allspice'),
('ginger powder'),
('cloves'),

-- Nuts & Seeds
('almonds'),
('walnuts'),
('pecans'),
('cashews'),
('peanuts'),
('pistachios'),
('pine nuts'),
('chia seeds'),
('flax seeds'),
('sesame seeds'),
('sunflower seeds'),

-- Misc
('mayonnaise'),
('ketchup'),
('mustard'),
('dijon mustard'),
('barbecue sauce'),
('tomato paste'),
('tomato sauce'),
('crushed tomatoes'),
('salsa'),
('guacamole'),
('pickles');

SELECT
    i.id,
    i.name,
    u.fdcid,
    u.name AS usda_name
FROM ingredients i
JOIN LATERAL (
    SELECT fdcid, name
    FROM usda_ingredients
    WHERE LOWER(name) LIKE '%' || LOWER(i.name) || '%'
    ORDER BY LENGTH(name)
    LIMIT 1
) u ON true;

INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit)
VALUES
-- Pancakes
(113893, 1, 1.5, 'cups'),   -- flour
(114869, 2, 1, 'cup'),      -- milk
(113675, 3, 1, 'unit'),     -- egg
(116709, 4, 1, 'tbsp'),     -- sugar
(66061, 5, 1, 'tsp'),      -- baking powder
(116282, 6, 0.5, 'tsp'),    -- salt
(112075, 7, 2, 'tbsp'),     -- butter

-- Spaghetti Bolognese
(2, 8, 1, 'lb'),       -- ground beef
(2, 9, 1, 'unit'),     -- onion
(2, 10, 2, 'cloves'),  -- garlic
(2, 11, 28, 'oz'),     -- crushed tomatoes
(2, 12, 1, 'lb'),      -- spaghetti
(2, 13, 1, 'tsp'),     -- oregano
(2, 14, 1, 'tsp'),     -- basil

-- Caesar Salad
(3, 15, 2, 'cups'),    -- romaine lettuce
(3, 16, 1, 'unit'),    -- chicken breast
(3, 17, 0.25, 'cup'),  -- parmesan
(3, 18, 0.25, 'cup'),  -- croutons
(3, 19, 3, 'tbsp'),    -- caesar dressing

-- Cookies
(4, 1, 2.25, 'cups'),  -- flour
(4, 3, 2, 'units'),    -- eggs
(4, 4, 0.75, 'cup'),   -- sugar
(4, 20, 0.75, 'cup'),  -- brown sugar
(4, 7, 1, 'cup'),      -- butter
(4, 21, 2, 'cups'),    -- chocolate chips
(4, 22, 1, 'tsp'),     -- vanilla extract

-- Omelette
(5, 3, 2, 'units'),    -- eggs
(5, 9, 0.25, 'unit'),  -- onion
(5, 23, 0.25, 'cup'),  -- bell pepper
(5, 17, 0.25, 'cup');  -- parmesan